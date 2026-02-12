import java.io.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class ViewReportServlet extends HttpServlet {

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

            
            String query = "SELECT id, report_status FROM test_bookings WHERE username = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            rs = stmt.executeQuery();

            List<Integer> appointmentIds = new ArrayList<>();
            Map<Integer, String> reportStatuses = new HashMap<>();

            while (rs.next()) {
                int id = rs.getInt("id");
                String reportStatus = rs.getString("report_status");
                appointmentIds.add(id);
                reportStatuses.put(id, reportStatus);
            }

           
            out.println("<!DOCTYPE html>");
            out.println("<html lang='en'>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("<title>View Report</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Select Appointment ID</h1>");

            if (appointmentIds.isEmpty()) {
                out.println("<p>No bookings found for " + username + ". Please book one!</p>");
            } else {
                out.println("<form method='post'>"); // Use POST to handle ID selection
                out.println("<label for='appointment_id'>Select ID: </label>");
                out.println("<select name='appointment_id' id='appointment_id'>");

                
                for (Integer id : appointmentIds) {
                    String status = reportStatuses.get(id);
                    out.println("<option value='" + id + "'>ID: " + id + " (Status: " + status + ")</option>");
                }

                out.println("</select>");
                out.println("<button type='submit'>View Report</button>");
                out.println("</form>");
            }

            out.println("</body></html>");

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            out.println("<p>Error fetching appointment IDs: " + e.getMessage() + "</p>");
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
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

     
        String appointmentId = req.getParameter("appointment_id");

        if (appointmentId == null || appointmentId.isEmpty()) {
            out.println("<p>No appointment selected. Please go back and choose one.</p>");
            return;
        }

        
        res.sendRedirect("report_final?id=" + appointmentId);
    }
}
