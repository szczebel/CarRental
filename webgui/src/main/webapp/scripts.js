function check() {
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.onreadystatechange = function() {
        if (xmlhttp.readyState == 4) {
                alert(xmlhttp.responseText);
            }
    };
    xmlhttp.open("POST", "/json/testconnection");
    xmlhttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    xmlhttp.send(JSON.stringify({handshake:"qqq"}));
}
