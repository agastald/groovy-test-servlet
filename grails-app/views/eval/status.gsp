<html>
<head>
	<title>Groovy Test Servlet</title>
	<meta http-equiv='refresh' content='600'></meta>
</head>
<style>
body {
    background-color: #e4f0f0;
    margin: 10px;
}
progress {
    width: 50px;
}
table {
    background-color: #ffffff;
    border-collapse: collapse;
    border: 1px solid #aca899;
}
tr:nth-child(even) {
    background-color: white;
}
tr:nth-child(odd) {
    background-color: ffffcc;
}
tr td:nth-child(5) { text-align:left; }
td {
    padding: 4px;
    text-align: right;
    vertical-align:top;
    border: 1px solid #aca899;
}
th {
    font-weight: normal;
    text-align: center;
    background-color: #ece9d8;
    padding: 4px;
    border: 1px solid #aca899;
    text-decoration: none;
    color: #0000ff;
}
div.resumo span {
	padding-right: 20px;
}
</style>
<body>

<table>
	<g:each in="${map}" var="k,v">
		<tr>
			<td>${k}</td>
			<td style="text-align:left;">${
				(v instanceof Map)
				 ? raw(v.entrySet().join('<br>'))
				 : (v instanceof List)
				  ? raw(v.join('<br>'))
				  : v
			}</td>
		</tr>
	</g:each>
</table>

</body>
</html>

