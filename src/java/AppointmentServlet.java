import java.io.*;
import java.sql.*;
import java.util.Random;
import javax.servlet.*;
import javax.servlet.http.*;

public class AppointmentServlet extends HttpServlet {

    private Random random = new Random(); 

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

       
        String doctorSpecialty = req.getParameter("doctor_specialty");
        String appointmentDate = req.getParameter("appointment_date");

        
        if (doctorSpecialty == null || appointmentDate == null || doctorSpecialty.isEmpty() || appointmentDate.isEmpty()) {
            out.println("<h3>Error: All fields are required!</h3>");
            return;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/harsh", "root", "12345678");

            
            String checkQuery = "SELECT COUNT(*) FROM appointments WHERE username = ? AND appointment_date = ?";
            stmt = conn.prepareStatement(checkQuery);
            stmt.setString(1, username);
            stmt.setDate(2, Date.valueOf(appointmentDate));
            rs = stmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                displayMessage(out, username, "Sorry, you already have an appointment on that date. Please choose another date.");
                return;
            }

            boolean canBook = random.nextBoolean(); 

            if (!canBook) {
                displayMessage(out, username, "Sorry, we can't provide an appointment on that date. Please choose another date.");
                return;
            }

           
            String query = "INSERT INTO appointments (username, appointment_date, doctor) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setDate(2, Date.valueOf(appointmentDate));
            stmt.setString(3, doctorSpecialty);
            int rowsInserted = stmt.executeUpdate();

           
            if (rowsInserted > 0) {
                displayMessage(out, username, "Appointment booked successfully with " + doctorSpecialty + "!");
            } else {
                out.println("<h3>Error booking appointment. Please try again.</h3>");
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            out.println("<h3>Database error: " + e.getMessage() + "</h3>");
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        out.close();
    }

    private void displayMessage(PrintWriter out, String username, String message) {
        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Appointment Response</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; background-color: #f5f5f5; text-align: center; padding: 50px; }");
        out.println("header { background-color: #003d75; color: white; padding: 20px; font-size: 36px; font-weight: bold; animation: moveHeader 3s infinite; }");
        out.println("@keyframes moveHeader {");
        out.println("0% { transform: translateX(0); }");
        out.println("25% { transform: translateX(-5px); }");
        out.println("50% { transform: translateX(0); }");
        out.println("75% { transform: translateX(5px); }");
        out.println("100% { transform: translateX(0); }");
        out.println("}");
        out.println(".message-box { background-color: white; border: 1px solid #ccc; border-radius: 10px; padding: 20px; box-shadow: 0 0 15px rgba(0, 0, 0, 0.2); display: inline-block; margin-top: 20px; }");
        out.println("h3 { margin: 20px; font-size: 24px; color: #003d75; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<header>EGGN Health Care - Your Health is Our Priority</header>");
        out.println("<div class='message-box'>");
        out.println("<h3>Hey " + username + ",</h3>");
        out.println("<h3>" + message + "</h3>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        doGet(req, res);
    }
}
