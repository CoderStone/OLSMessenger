package com.olsmessenger.database;

import static spark.Spark.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import com.olsmessenger.database.tables.User;
import com.olsmessenger.database.tables.Class;
import com.olsmessenger.database.tables.Class.ChatLine;

public class SparkPost {
	private final static String key="scooterman";
	private static DatabaseInterface databaseInterface;
	public static void main(String args[])
	{
		initDb();
		get("/signup",(req,res)->"hi!");
		get("/login",(req,res)->"hi!");
		get("/chatupdate",(req,res)->"hi!");
		get("/chatpull",(req,res)->"hi!");
		get("/classes",(req,res)->"hi!");
		get("/verifyID",(req,res)->"hi!");
		post("/login", (request, response) -> {
			if(!validate(request.queryParams("key")))return "Failed, wrong auth key.";
			String email=request.queryParams("email");
			String password=request.queryParams("password");
			String username=email.substring(0,email.indexOf("@"));
			return verifyUser(username,password);
		});
		post("/signup", (request, response) -> {
			if(!validate(request.queryParams("key")))return "Failed, wrong auth key.";
			//initial query processing
			String name=request.queryParams("name");
			String email=request.queryParams("email");
			String password=request.queryParams("password");
			String c=request.queryParams("classes");
			//additional query processing
			if(name.indexOf(' ')==-1)return "fail!";
			String firstname=name.substring(0,name.indexOf(" "));
			String lastname=name.substring(name.indexOf(" ")+1);
			String username=email.substring(0,email.indexOf("@"));
			String[] classs=c.split("!");
			List<String> classes = new ArrayList<String>();
			for(String i:classs)if(i.charAt(0)!='F'&&i.charAt(1)!='R')classes.add(i);
			//database input
			if(createUser(firstname,lastname,username,classes,password))return "successful!";
			return "fail!";
		});
		post("/chatupdate", (request, response) -> {
			if(!validate(request.queryParams("key")))return "Failed, wrong auth key.";
			String email=request.queryParams("email");
			String classs=request.queryParams("class");
			String content=request.queryParams("chat");
			String username=email.substring(0,email.indexOf("@"));
			Class cla=databaseInterface.getClassByName(classs).get();
			ChatLine ch = new ChatLine();
			ch.setLine(content);
			ch.setSender(databaseInterface.getUserByUsername(username).get().getId());
			ch.setTimestamp(new java.util.Date().getTime());
			List<ChatLine> dddd=cla.getChatHistory();
			dddd.add(ch);
			cla.setChatHistory(dddd);
			databaseInterface.saveClass(cla);
		    return "successful!";
		});
		post("/chatpull", (request, response) -> {
			if(!validate(request.queryParams("key")))return "Failed, wrong auth key.";
			String email=request.queryParams("email");
			String classs=request.queryParams("class");
			Class cla=databaseInterface.getClassByName(classs).get();
			List<ChatLine> dddd=cla.getChatHistory();
			String retun="";
			for(int i=(0>dddd.size()-100?0:dddd.size()-100);i<dddd.size();i++)
			{
				ChatLine j=dddd.get(i);
				String content=j.getLine();
				long time=j.getTimestamp();
				int id=j.getSender();
				retun= retun+((Integer)id).toString()+"|"+((Long)time).toString()+"|"+content+"|";
			}
		    return retun;
		});
		post("/classes", (request, response) -> {
			if(!validate(request.queryParams("key")))return "Failed, wrong auth key.";
			String email=request.queryParams("email");
			String username=email.substring(0,email.indexOf("@"));
		    return getClassesByUser(username);
		});
		post("/verifyID", (request, response) -> {
			if(!validate(request.queryParams("key")))return "Failed, wrong auth key.";
			String email=request.queryParams("email");
			String id=request.queryParams("id");
			String[] ID=id.split("\\|");
			String username=email.substring(0,email.indexOf("@"));
			return verifyUser(username,ID);
		});
	}
	private static void initDb()
	{
		databaseInterface = new DatabaseInterface();
        databaseInterface.connect();
	}
	private static boolean validate(String key2)
	{
		return key.equals(key2);
	}
	private static boolean createUser(String firstname,String lastname,String username,List<String>classes,String password)
	{
		boolean[] repeat=new boolean[2];repeat[0]=true;
		databaseInterface.getAllUsers().forEach((user)->{if((user.getUsername()).equals(username))repeat[0]=false;});
		if(repeat[0]==false)return false;
		User user = new User();
        user.setFirstName(firstname);
        user.setLastName(lastname);
        user.setPassword(password);
        user.setUsername(username);
        user.setClasses(classes);
        databaseInterface.addUser(user); // this is how to add a user
//        databaseInterface.saveUser(user); We don't need to save it after add if there's no change to it
        databaseInterface.getAllUsers().forEach(System.out::println);
        addWithoutDuplicateClass(classes,user.getId());
        return true;
	}
	private static String verifyUser(String username,String password)
	{
		try {
		User user=databaseInterface.getUserByUsername(username).get();
		if(user==null)return "fail!";
		if(password.equals(user.getPassword()))return user.getFirstName()+" "+user.getLastName();
		return "fail!";}
		catch(NoSuchElementException e){
			return "fail!";
		}
	}
	private static String verifyUser(String username,String[] id)
	{
		try {
		User user=databaseInterface.getUserByUsername(username).get();
		if(user==null)return "failnull!";
		String resp="";
		for(String i:id)
		{
			user=databaseInterface.getUserById(Integer.parseInt(i)).get();
			resp+=user.getFirstName()+" "+user.getLastName()+"|";
		}
		return resp;}
		catch(NoSuchElementException e){
			return "failoops!";
		}
	}
	private static String getClassesByUser(String username)
	{
		String fail;
		fail="fail!";
		try {
			User user=databaseInterface.getUserByUsername(username).get();
			if(user==null)return fail;
			List<String> clas=user.getClasses();
			int i=0;String z="";
			for(String l:clas)z=z+l+'!';
			return z;
		}
			catch(NoSuchElementException e){
				return fail;
			}
	}
	private static void addWithoutDuplicateClass(List<String> classes, int id)
	{
		for(String i:classes)
		{
			try {
				Class cla=databaseInterface.getClassByName(i).get();
				List<Integer> students=cla.getUserIds();
				students.add(id);
				cla.setUserIds(students);
				databaseInterface.saveClass(cla);
			}
				catch(NoSuchElementException e){
					Class cla=new Class();
					cla.setClassName(i);
					List<Integer> students=cla.getUserIds();
					students.add(id);
					cla.setUserIds(students);
					databaseInterface.addClass(cla);
				}
		}
		databaseInterface.getAllClasses().forEach(System.out::println);
	}
}
