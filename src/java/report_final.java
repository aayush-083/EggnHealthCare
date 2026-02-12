import java.io.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class report_final extends HttpServlet {

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
            out.println("<p>Error: Unable to retrieve user details. Please log in again.</p>");
            return;
        }

     
        String appointmentId = req.getParameter("id");
        if (appointmentId == null || appointmentId.isEmpty()) {
            out.println("<p>Error: Appointment ID is missing.</p>");
            return;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/harsh", "root", "12345678");

           
            String query = "SELECT test_name, report_status FROM test_bookings WHERE id = ?";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, Integer.parseInt(appointmentId));
            rs = stmt.executeQuery();

            if (rs.next()) {
                String testName = rs.getString("test_name");
                String reportStatus = rs.getString("report_status");

               
                if (!"done".equalsIgnoreCase(reportStatus)) {
                    out.println("<p>Your report is still in progress. Please check back later.</p>");
                } else {
                    // Generate a professional report based on the test type
                    out.println("<!DOCTYPE html>");
                    out.println("<html lang='en'>");
                    out.println("<head>");
                    out.println("<meta charset='UTF-8'>");
                    out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
                    out.println("<title>Test Report</title>");
                    out.println("<style>");
                    out.println("body { font-family: Arial, sans-serif; background-color: #f9f9f9; color: #333; margin: 0; padding: 0; }");
                    out.println(".header { background-color: #003d75; color: white; padding: 20px; text-align: center; }");
                    out.println(".header h1 { margin: 0; font-size: 24px; }");
                    out.println(".container { max-width: 900px; margin: 30px auto; background: white; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1); padding: 20px; }");
                    out.println("h2 { color: #003d75; margin-bottom: 20px; }");
                    out.println("table { width: 100%; border-collapse: collapse; margin: 20px 0; }");
                    out.println("table, th, td { border: 1px solid #ddd; }");
                    out.println("th, td { padding: 10px; text-align: left; }");
                    out.println("th { background-color: #003d75; color: white; }");
                    out.println(".footer { text-align: center; margin-top: 20px; font-size: 12px; color: gray; }");
                    out.println("</style>");
                    out.println("</head>");
                    out.println("<body>");
                    out.println("<div class='header'>");
                    out.println("<h1>EGGN Healthcare</h1>");
                    out.println("<p>Comprehensive Health Solutions</p>");
                    out.println("</div>");
                    out.println("<div class='container'>");
                    out.println("<h2>Health Report</h2>");
                    out.println("<p><strong>Patient Name:</strong> " + username + "</p>");
                    out.println("<p><strong>Appointment ID:</strong> " + appointmentId + "</p>");
                    out.println("<p><strong>Test Name:</strong> " + testName + "</p>");

                    // Generate a random report based on test_name
                    Random rand = new Random();
                    out.println("<h3>Report Details</h3>");
                    out.println("<table>");

                    switch (testName.toLowerCase()) {
                        case "general":
                            out.println("<tr><th>Parameter</th><th>Value</th></tr>");
                            out.println("<tr><td>Heart Rate</td><td>" + (rand.nextInt(40) + 60) + " bpm</td></tr>");
                            out.println("<tr><td>Blood Pressure</td><td>" + (rand.nextInt(50) + 90) + "/" + (rand.nextInt(30) + 60) + " mmHg</td></tr>");
                            break;

                        case "cardiology":
                            out.println("<tr><th>Parameter</th><th>Value</th></tr>");
                            out.println("<tr><td>ECG</td><td>Normal</td></tr>");
                            out.println("<tr><td>Cholesterol Level</td><td>" + (rand.nextInt(100) + 100) + " mg/dL</td></tr>");
                            break;

                        case "diabetes":
                            out.println("<tr><th>Parameter</th><th>Value</th></tr>");
                            out.println("<tr><td>Blood Sugar Level</td><td>" + (rand.nextInt(120) + 60) + " mg/dL</td></tr>");
                            out.println("<tr><td>HbA1c</td><td>" + String.format("%.1f", rand.nextDouble() * 2 + 4.5) + "%</td></tr>");
                            break;

                        default:
                            out.println("<tr><td colspan='2'>No specific report available for this test type.</td></tr>");
                            break;
                    }

                    out.println("</table>");
                    out.println("<p><strong>Doctor's Notes:</strong> Your health report indicates no critical concerns. Maintain a balanced diet and regular exercise.</p>");
                    out.println("</div>");
                    out.println("<div class='footer'>");
                    out.println("&copy; 2024 EGGN Healthcare | Contact: support@eggnhealth.com | All rights reserved.");
                    out.println("</div>");
                    out.println("</body>");
                    out.println("</html>");
                }
            } else {
                out.println("<p>No report found for Appointment ID: " + appointmentId + "</p>");
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            out.println("<p>Error generating report: " + e.getMessage() + "</p>");
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        out.close();
    }
}
