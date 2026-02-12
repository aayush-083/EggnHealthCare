import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class MainServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        HttpSession session = req.getSession(false);  
        String username = null;

        if (session != null) {
            username = (String) session.getAttribute("username");  
        } 

        if (username == null) {
            Cookie[] cookies = req.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("username")) {
                        username = cookie.getValue();
                    }
                }
            }
        }

        if (username == null) {
            res.sendRedirect("login.html");  
            return;
        }

        String fullName = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/harsh", "root", "12345678");

            String query = "SELECT name FROM users WHERE username = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            rs = stmt.executeQuery();

            if (rs.next()) {
                fullName = rs.getString("name");
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

      
        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>EGGN Health Care - Dashboard</title>");
        out.println("<style>");
        out.println("* { margin: 0; padding: 0; box-sizing: border-box; }");
        out.println("html, body { height: 100%; font-family: Arial, sans-serif; display: flex; flex-direction: column; justify-content: space-between; text-align: center; }");
        out.println("body { background: url('') no-repeat center center fixed; background-size: cover; }"); // Background image style
        out.println("header { background-color: rgba(0, 61, 117, 0.8); color: white; padding: 20px; font-size: 36px; font-weight: bold; width: 100%; text-align: center; overflow: hidden; animation: moveHeader 4s infinite; }");
        out.println("@keyframes moveHeader {");
        out.println("0% { transform: translateX(0); }");
        out.println("25% { transform: translateX(-5px); }");
        out.println("50% { transform: translateX(0); }");
        out.println("75% { transform: translateX(5px); }");
        out.println("100% { transform: translateX(0); }");
        out.println("}");
        out.println(".container { display: flex; flex-direction: column; align-items: center; justify-content: center; flex-grow: 1; position: relative; z-index: 1; }");
        out.println(".panel { background-color: rgba(255, 255, 255, 0.9); border: 2px solid #003d75; border-radius: 10px; padding: 40px; box-shadow: 0 0 20px rgba(0, 0, 0, 0.1); width: 80%; max-width: 600px; position: relative; }");
        out.println(".panel h1 { font-size: 28px; color: #003d75; margin-bottom: 20px; }");
        out.println(".panel a { display: inline-block; padding: 15px 25px; margin: 10px; background-color: #003d75; color: white; text-decoration: none; border-radius: 5px; font-size: 18px; transition: background-color 0.3s, transform 0.3s; }");
        out.println(".panel a:hover { background-color: #00264d; transform: translateY(-3px); box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2); }");
        out.println("footer { background-color: rgba(0, 61, 117, 0.8); color: white; padding: 10px; font-size: 14px; width: 100%; text-align: center; position: absolute; bottom: 0; z-index: 1; }");
        out.println(".menu-icon { position: absolute; top: 20px; right: 20px; cursor: pointer; font-size: 24px; z-index: 1; }");
        out.println(".dropdown-menu { display: none; position: absolute; top: 60px; right: 20px; background-color: white; border: 1px solid #ccc; border-radius: 5px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.2); z-index: 100; }");
        out.println(".dropdown-menu a { display: block; padding: 10px; text-decoration: none; color: #003d75; }");
        out.println(".dropdown-menu a:hover { background-color: #f0f0f0; }");
        out.println(".three-dots { display: inline-block; cursor: pointer; }");
        out.println(".three-dots:after { content: '...'; font-size: 24px; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");

        out.println("<header>EGGN Health Care - Your Health is Our Priority</header>");

        out.println("<div class='container'>");  
        if (fullName != null) {
            out.println("<div class='greeting'>Welcome, " + fullName + "!</div>"); 
        } else {
            out.println("<div class='greeting'>Welcome, " + username + "!</div>");  
        }

        
        out.println("<div class='panel'>");
        out.println("<h1>Dashboard</h1>");  

        
        out.println("<a href='BookServlet'>Book a Test</a>");
        out.println("<a href='appointment.html'>Book an Appointment</a>"); 
        out.println("<a href='ViewReportServlet'>View Report</a>");
        out.println("</div>");

      
        out.println("<div class='menu-icon three-dots' onclick='toggleMenu()'></div>");

        
        out.println("<div class='dropdown-menu' id='dropdownMenu'>");
        out.println("<a href='checkAppointmentServlet'>Check Appointment Date</a>");
        out.println("<a href='CheckReportStatusServlet'>Check Report Status</a>");
        out.println("<a href='ViewReportServlet'>View Reports</a>");
        out.println("<a href='logoutServlet'>Logout</a>");
        out.println("</div>");

        out.println("</div>");  
        out.println("<footer>&copy; 2024 EGGN Health Care | Contact: @221031013@gmail.com</footer>");

        out.println("<script>");
        out.println("function toggleMenu() {");
        out.println("    var menu = document.getElementById('dropdownMenu');");
        out.println("    menu.style.display = (menu.style.display === 'block') ? 'none' : 'block';");
        out.println("}");
        
        out.println("window.onclick = function(event) {");
        out.println("    if (!event.target.matches('.menu-icon')) {");
        out.println("        var dropdowns = document.getElementsByClassName('dropdown-menu');");
        out.println("        for (var i = 0; i < dropdowns.length; i++) {");
        out.println("            dropdowns[i].style.display = 'none';");
        out.println("        }");
        out.println("    }");
        out.println("};");
        out.println("</script>");

        out.println("</body>");
        out.println("</html>");

        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        doGet(req, res);  
    }
}
