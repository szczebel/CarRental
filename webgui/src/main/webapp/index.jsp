<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <title>Car Rental</title>
    </head>

    <body>
        <form method="get" action="/view/home">
            <input type="submit" value="Login"/>
        </form>
        <h2>Or</h2>
        <form method="post" action="/register">
            Enter your email: <input type="email" name="email"><br/><br/>
            Enter your name: <input type="text" name="name"><br/><br/>
            <input type="submit" value="Register">
        </form>

    </body>
</html>