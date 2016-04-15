<html>
<head><title>Groovy Test Servlet</title></head>
<script type="text/javascript">
function keyDown(event) {
    var key=event.keyCode? event.keyCode : event.charCode;
    if (key == null) {
        return false;
    }
    if (key == 13 && (event.ctrlKey || event.metaKey)) {
        // ctrl + return, cmd + return
        event.target.parentElement.submit();
        return false;
    }
}
function doFocus() {
    document.getElementById("groovy.script").focus();
}
</script>
<body onload="doFocus();">

<h1 style='color:blue;'>Groovy Test Servlet</h1>
Groovy version: ${GroovySystem.version}

<g:if test="${evaluate}">
<h2>Script Output</h2>
<table width="80%" border="1" cellpadding="6"><tr><td bgcolor="#eeeeee">
<pre>
${output}
</pre>
</td></tr></table>

<h2>Script Return Value</h2>
<pre>
${result}
</pre>
</p>
</g:if>

<h2>Script</h2>
<form method="POST" action="">
<TEXTAREA id="groovy.script" name="groovy.script" rows="10" cols="80" onkeydown="return keyDown(event)">
${params.'groovy.script'}
</TEXTAREA>
<p>
Capture Stdout/Stderr:
<INPUT type="checkbox" name="groovy.servlet.captureOutErr" value="true" <g:if test="${params.'groovy.servlet.captureOutErr'}">checked</g:if>>
Display Raw Output:
<INPUT type="checkbox" name="groovy.servlet.output" value="raw" <g:if test="${params.'groovy.servlet.output'}">checked</g:if>>
<p>
<INPUT type="submit" value="Evaluate">
</form>

</body>
</html>
