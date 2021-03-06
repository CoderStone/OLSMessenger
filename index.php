<!DOCTYPE html>
<html lang="en">
    
<head>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/css/bootstrap.min.css" integrity="sha384-TX8t27EcRE3e/ihU7zmQxVncDAy5uIKz4rEkgIXeMed4M0jlfIDPvg6uqKI2xXr2" crossorigin="anonymous">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>OLS Messenger</title>
    <style>
        body {
            padding: 20px;
        }

        #logo {
            display: block;
            margin-left: auto;
            margin-right: auto;
            width: 10%;
        }
    </style>
</head>
    
<?php 
include 'variables.php';
if (!session_id()) session_start();
if (isset($_SESSION['login'])){ 
    header("Location:main.php");
    die();
}
?>
    
<body>
    <img src="logo.png" alt="exeter logo" id="logo">

    <h1 style="text-align: center">Exeter Messenger</h1>
    <h5 style="text-align: center; color: grey">Sign up or Log in to chat, connect and share with the PEA community.</h1>
    <button onclick="document.getElementById('signup').style.display='inline';document.getElementById('login').style.display='none';" class="btn btn-primary" style="background-color:#862727!important;">Sign Up</button>
    <button onclick="document.getElementById('signup').style.display='none';document.getElementById('login').style.display='inline';" class="btn btn-primary" style="background-color:#862727!important;">Log In</button>
    <div style="display:none;" id="signup">
    <form action="handler.php" method="post">
        <p>
        <div class="form-group">
            <label for="name">What's your name?</label>
            <input type="text" class="form-control" id="name" name="name">
        </div>
        <div class="form-group">
            <label for="name">What's your email?</label>
            <input type="email" class="form-control" id="email" name="email">
        </div>
        <div class="form-group">
            <label for="name">Password.</label>
            <input type="password" class="form-control" id="password" name="password">
        </div>
        <div class="form-group">
            <label for="schedule">Please paste your exported OLS schedule here.</label>
            <input type="text" class="form-control" id="schedule" name="schedule">
        </div>
        <button type="submit" class="btn btn-primary" style="background-color:#862727!important;">Sign Up</button>
        </p>
    </form>

    <br><br><br>

    <h3>How do I submit my exported OLS schedule?</h3>
    <ol>
        <li>Go to OLS</li>
        <li>In the top left corner, click the hamburger and then click 'Export'.</li>
        <li>Select 'All Formats'.</li>
        <li>Scroll and click 'Export'.</li>
        <li>Open the '.ics' file in a text editor like Notepad.</li>
        <li>Copy, paste and submit.</li>
    </ol>
    </div>
    <div style="display:none;" id="login">
    <form action="handler2.php" method="post">
        <p>
        <div class="form-group">
            <label for="name">Email</label>
            <input type="email" class="form-control" id="email" name="email">
        </div>
        <div class="form-group">
            <label for="name">Password.</label>
            <input type="password" class="form-control" id="password" name="password">
        </div>
        <button type="submit" class="btn btn-primary" style="background-color:#862727!important;">Log In</button>
        </p>
    </form>
</body>
</html>
