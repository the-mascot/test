<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<%
	int sum=0;
	for(int i=1; i<=100; i++) {
		sum+=i;
	}
	request.setAttribute("sum", sum);
	request.setAttribute("sum1", sum);
	RequestDispatcher rd=request.getRequestDispatcher("hunResult.jsp");
	rd.forward(request, response);
%>
${sum}<p>
${sum3}
</body>
</html>