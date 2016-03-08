function check() {
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.onreadystatechange = function() {
        if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
                alert(xmlhttp.responseText);
            }
    };
    xmlhttp.open("GET", "/rest/check", true);
    xmlhttp.send();
}
