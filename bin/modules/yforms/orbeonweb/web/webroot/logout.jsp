<%session.invalidate();%>
<% HttpSession nsession = request.getSession(false);
if(nsession!=null) {
   out.println("Session is still active");
}
else
  out.println("Session is not active");
%>