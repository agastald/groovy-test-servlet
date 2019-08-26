<html>
<head>
    <title>Eval Servlet</title>
    <style type="text/css">
        #td_submit { height: 1px; }
        #tr_script { height: 1px; }
        #td_options { width: 1px; white-space: nowrap; }
    </style>
<script type="text/javascript">
function keyDown(event) {
    var key = event.keyCode? event.keyCode : event.charCode;
    if (key == null) {
        return false;
    }
    // shift + return
    if (key == 13 && event.shiftKey) {
        event.target.form.submit();
        return false;
    }
}
function doFocus() {
    var textarea = document.getElementById("groovy.script");
    textarea.focus();
    textarea.setSelectionRange(textarea.textLength,textarea.textLength);
}
</script>
</head>
<body onload='doFocus()'>

<form method="POST" target="result">
<table id="main" width="100%" height="100%" border="0">
<tr id="tr_script" valign="top"><td rowspan="2">
<TEXTAREA id="groovy.script" name="groovy.script" rows="10" cols="80" style="width:100%;" onkeydown="return keyDown(event)" tabindex="1">
</TEXTAREA>
</td><td id='td_options'>
<div style='color:blue;'>Groovy version: <b>${GroovySystem.version}</b></div>
<INPUT type="checkbox" tabindex="3" name="groovy.servlet.captureOutErr" checked value="true"> Capture Stdout/Stderr<br>
%{-- <INPUT type="checkbox" tabindex="4" name="groovy.servlet.output" value="raw"> Display Raw Output<br> --}%
<INPUT type="checkbox" tabindex="5" name="groovy.servlet.lib" checked value="true"> Include lib<br>
</td></tr><tr><td id="td_submit">
<INPUT type="submit" value="Evaluate" tabindex="2"><br>
</td></tr>
<tr><td colspan="2">
<iframe name="result" style="border:1px solid black; white-space:pre;" width="100%" height="100%"></iframe>
</td></tr>
</table>
</form>

</body>
<html>
