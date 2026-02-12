import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class checkAppointmentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        
        String username = null;
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("username".equals(cookie.getName())) {
                    username = cookie.getValue();
                    break;
                }
            }
        }

      
        if (username == null) {
            res.sendRedirect("login.html");
            return;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/harsh", "root", "12345678");

            
            String query = "SELECT appointment_date FROM appointments WHERE username = ?";
            stmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setString(1, username);
            rs = stmt.executeQuery();

            
            out.println("<!DOCTYPE html>");
            out.println("<html lang='en'>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("<title>EGGN Health Care - Appointment Details</title>");
            out.println("<style>");
            out.println("* { margin: 0; padding: 0; box-sizing: border-box; }");
            out.println("html, body { height: 100%; font-family: Arial, sans-serif; display: flex; flex-direction: column; justify-content: space-between; text-align: center; }");
            out.println("body { background: url('path/to/your/background-image.jpg') no-repeat center center fixed; background-size: cover; }");
            out.println("header { background-color: rgba(0, 61, 117, 0.8); color: white; padding: 20px; font-size: 36px; font-weight: bold; width: 100%; text-align: center; }");
            out.println(".container { display: flex; flex-direction: column; align-items: center; justify-content: center; flex-grow: 1; position: relative; z-index: 1; }");
            out.println(".panel { background-color: rgba(255, 255, 255, 0.9); border: 2px solid #003d75; border-radius: 10px; padding: 40px; box-shadow: 0 0 20px rgba(0, 0, 0, 0.1); width: 80%; max-width: 600px; position: relative; }");
            out.println(".panel h1 { font-size: 28px; color: #003d75; margin-bottom: 20px; }");
            out.println(".panel p { font-size: 20px; color: #333; }");
            out.println(".panel ul { list-style-type: none; padding: 0; }");
            out.println(".panel ul li { margin: 10px 0; }");
            out.println("footer { background-color: rgba(0, 61, 117, 0.8); color: white; padding: 10px; font-size: 14px; width: 100%; text-align: center; position: absolute; bottom: 0; z-index: 1; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");

            out.println("<header>EGGN Health Care - Appointment Details</header>");
            out.println("<div class='container'>");

            out.println("<div class='panel'>");
            out.println("<h1>Your Appointments</h1>");

          
            if (rs.next()) {
               
                out.println("<ul>");
                do {
                    String appointmentDate = rs.getString("appointment_date");
                    out.println("<li>Appointment on: <strong>" + appointmentDate + "</strong></li>");
                } while (rs.next());
                out.println("</ul>");
            } else {
                out.println("<p>Sorry " + username + ", you do not have any appointments scheduled. Please book one!</p>");
            }

            out.println("</div>");
            out.println("</div>");
            out.println("<footer>&copy; 2024 EGGN Health Care | Contact: @221031013@gmail.com</footer>");

            out.println("</body>");
            out.println("</html>");

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            out.println("<p>Error retrieving appointments: " + e.getMessage() + "</p>");
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        doGet(req, res);
    }
}
