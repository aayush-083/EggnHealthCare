import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.Random;
import javax.servlet.*;
import javax.servlet.http.*;

public class BookServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

       
        String cookieUsername = null;
        Cookie[] cookies = req.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("username")) {
                    cookieUsername = cookie.getValue();
                    break;
                }
            }
        }

        if (cookieUsername == null) {
            res.sendRedirect("login.html");
            return;
        }

        
        String testName = req.getParameter("test_name");

        if (testName == null || testName.isEmpty()) {
            
            out.println("<html><head><title>Book a Test</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; background-color: #f5f5f5; }");
            out.println(".marquee { width: 100%; overflow: hidden; white-space: nowrap; background-color: #003d75; color: white; padding: 10px; }");
            out.println(".marquee div { display: inline-block; padding-left: 100%; animation: marquee 15s linear infinite; }");
            out.println("@keyframes marquee { from { transform: translate(0); } to { transform: translate(-100%); } }");
            out.println(".container { max-width: 600px; margin: auto; padding: 20px; border-radius: 8px; background-color: white; box-shadow: 0 0 20px rgba(0, 0, 0, 0.1); text-align: center; }");
            out.println("h2 { color: #003d75; }");
            out.println("label { font-weight: bold; }");
            out.println("select, input[type='submit'] { width: 100%; padding: 10px; margin: 10px 0; border: 1px solid #ccc; border-radius: 4px; }");
            out.println("input[type='submit'] { background-color: #003d75; color: white; border: none; cursor: pointer; }");
            out.println("input[type='submit']:hover { background-color: #00264d; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class='marquee'><div>Welcome to EggnhealthCare - Your Health is Our Priority!</div></div>");
            out.println("<div class='container'>");
            out.println("<h2>Select the test you want to book:</h2>");
            out.println("<form method='GET' action='BookServlet'>");
            out.println("<label for='test_name'>Choose Test:</label>");
            out.println("<select name='test_name' id='test_name'>");
            out.println("<option value='Blood Test'>Blood Test</option>");
            out.println("<option value='X-Ray'>X-Ray</option>");
            out.println("<option value='MRI Scan'>MRI Scan</option>");
            out.println("<option value='CT Scan'>CT Scan</option>");
            out.println("</select><br><br>");
            out.println("<input type='submit' value='Book Test'>");
            out.println("</form>");
            out.println("</div>");
            out.println("</body></html>");
            return;
        }

        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet userRs = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/harsh", "root", "12345678");

            
            String userQuery = "SELECT username FROM users WHERE username = ?";
            stmt = conn.prepareStatement(userQuery);
            stmt.setString(1, cookieUsername);
            userRs = stmt.executeQuery();

            String dbUsername = null;
            if (userRs.next()) {
                dbUsername = userRs.getString("username");
            }

           
            if (dbUsername == null) {
                res.sendRedirect("login.html");
                return;
            }

           
            LocalDate currentDate = LocalDate.now();
            LocalDate randomDate = getRandomDateWithinWeek(currentDate);

            
            String idQuery = "SELECT MAX(id) FROM test_bookings";
            stmt = conn.prepareStatement(idQuery);
            ResultSet idRs = stmt.executeQuery();

            int customId = 1231234123; 
            if (idRs.next()) {
                int maxId = idRs.getInt(1);
                customId = maxId + 1; 
            }

           
            String insertQuery = "INSERT INTO test_bookings (id, username, test_name, booking_date) VALUES (?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setInt(1, customId);
            insertStmt.setString(2, dbUsername);
            insertStmt.setString(3, testName);
            insertStmt.setDate(4, Date.valueOf(randomDate));

            int rowsInserted = insertStmt.executeUpdate();

            if (rowsInserted > 0) {
   
    out.println("<html><head><title>Test Booking Confirmation</title>");
    out.println("<style>");
    out.println("body { font-family: Arial, sans-serif; background-color: #f5f5f5; }");
    out.println(".container { max-width: 600px; margin: auto; padding: 20px; border-radius: 8px; background-color: white; box-shadow: 0 0 20px rgba(0, 0, 0, 0.1); text-align: center; }");
    out.println("h3 { color: #003d75; }");
    out.println("p { font-size: 16px; }");
    out.println("input[type='submit'] { background-color: #003d75; color: white; border: none; cursor: pointer; padding: 10px; border-radius: 4px; }");
    out.println("input[type='submit']:hover { background-color: #00264d; }");
    out.println("</style>");
    out.println("</head>");
    out.println("<body>");
    out.println("<div class='marquee'><div>Welcome to EggnhealthCare - Your Health is Our Priority!</div></div>");
    out.println("<div class='container'>");
    out.println("<h3>Test Booking Confirmation</h3>");
    out.println("<p>Your <strong>" + testName + "</strong> has been successfully scheduled.</p>");
    out.println("<p><strong>Booking Date:</strong> " + randomDate + "</p>");
    out.println("<p>Thank you for choosing us for your health needs.</p>");
    out.println("<form action='MainServlet' method='GET'>");
    out.println("<input type='submit' value='Back to Dashboard'>");
    out.println("</form>");
    out.println("</div>");
    out.println("</body></html>");
} else {
    out.println("<h3>Error booking the test. Please try again.</h3>");
}


        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            out.println("<h3>Error: " + e.getMessage() + "</h3>");
        } finally {
            try {
                if (userRs != null) userRs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        out.close();
    }

    
    private LocalDate getRandomDateWithinWeek(LocalDate startDate) {
        Random rand = new Random();
        int randomDays = rand.nextInt(7) + 1; 
        return startDate.plusDays(randomDays);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        doGet(req, res);
    }
}
