import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.time.LocalDate;
import java.util.Random;

public class CheckReportStatusServlet extends HttpServlet {

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

            
            String query = "SELECT booking_date, report_status FROM test_bookings WHERE username = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            rs = stmt.executeQuery();

            
            out.println("<!DOCTYPE html>");
            out.println("<html lang='en'>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("<title>EGGN Health Care - Report Status</title>");
            out.println("<style>");
            out.println("* { margin: 0; padding: 0; box-sizing: border-box; }");
            out.println("html, body { height: 100%; font-family: Arial, sans-serif; display: flex; flex-direction: column; justify-content: space-between; text-align: center; }");
            out.println("body { background: url('path/to/your/background-image.jpg') no-repeat center center fixed; background-size: cover; }");
            out.println("header { background-color: rgba(0, 61, 117, 0.8); color: white; padding: 20px; font-size: 36px; font-weight: bold; width: 100%; text-align: center; }");
            out.println(".container { display: flex; flex-direction: column; align-items: center; justify-content: center; flex-grow: 1; position: relative; z-index: 1; }");
            out.println(".panel { background-color: rgba(255, 255, 255, 0.9); border: 2px solid #003d75; border-radius: 10px; padding: 40px; box-shadow: 0 0 20px rgba(0, 0, 0, 0.1); width: 80%; max-width: 600px; position: relative; }");
            out.println(".panel h1 { font-size: 28px; color: #003d75; margin-bottom: 20px; }");
            out.println(".status { font-size: 20px; margin: 20px 0; }");
            out.println(".done { color: green; font-weight: bold; }");
            out.println(".in-progress { color: red; font-weight: bold; }");
            out.println(".button { margin-top: 20px; }");
            out.println(".button a { text-decoration: none; background-color: #003d75; color: white; padding: 10px 20px; border-radius: 5px; font-size: 18px; }");
            out.println(".button a:hover { background-color: #0059b3; }");
            out.println("footer { background-color: rgba(0, 61, 117, 0.8); color: white; padding: 10px; font-size: 14px; width: 100%; text-align: center; position: absolute; bottom: 0; z-index: 1; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");

            out.println("<header>EGGN Health Care - Report Status</header>");
            out.println("<div class='container'>");
            out.println("<div class='panel'>");
            out.println("<h1>Your Test Report Status</h1>");

            if (rs.next()) {
                LocalDate bookingDate = rs.getDate("booking_date").toLocalDate();
                String reportStatus = rs.getString("report_status");

               
                if (bookingDate.plusDays(2).isBefore(LocalDate.now())) {
                    reportStatus = "done";
                    // Update report_status column
                    updateReportStatus(conn, username, reportStatus);
                    out.println("<div class='status done'>Your report status: <strong>Done</strong></div>");

                   
                    out.println("<div class='button'>");
                    out.println("<a href='ViewReportServlet'>View Your Report</a>");
                    out.println("</div>");
                } else {
                    reportStatus = "in progress";
                    out.println("<div class='status in-progress'>Your report status: <strong>In Progress</strong></div>");
                }
            } else {
                
                out.println("<p>No test bookings found for " + username + ".</p>");
            }

            out.println("</div>");
            out.println("</div>");
            out.println("<footer>&copy; 2024 EGGN Health Care | Contact: @221031013@gmail.com</footer>");
            out.println("</body>");
            out.println("</html>");

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            out.println("<p>Error retrieving report status: " + e.getMessage() + "</p>");
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        out.close();
    }

    private void updateReportStatus(Connection conn, String username, String reportStatus) throws SQLException {
        
        String updateQuery = "UPDATE test_bookings SET report_status = ? WHERE username = ?";
        try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
            updateStmt.setString(1, reportStatus);
            updateStmt.setString(2, username);
            updateStmt.executeUpdate();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        doGet(req, res);
    }
}
