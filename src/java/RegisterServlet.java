import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class RegisterServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

     
        String name = req.getParameter("name");
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String specialKey = req.getParameter("special_key"); // New parameter for special_key

        Connection conn = null;
        PreparedStatement checkStmt = null;
        PreparedStatement insertStmt = null;
        ResultSet rs = null;

        try {
           
            Class.forName("com.mysql.jdbc.Driver");

            
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/harsh", "root", "12345678");

            
            String checkQuery = "SELECT username FROM users WHERE username = ?";
            checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, username);
            rs = checkStmt.executeQuery();

            if (rs.next()) {
                
                out.println("<html><head><title>Registration Error</title></head><body>");
                out.println("<h1 style='color:red;'>Error: Username '" + username + "' already exists. Please choose another username.</h1>");
                out.println("<p><a href='register.html'>Try again</a></p>");
                out.println("</body></html>");
            } else {
              
                String insertQuery = "INSERT INTO users (name, username, password, sp_key) VALUES (?, ?, ?, ?)";
                insertStmt = conn.prepareStatement(insertQuery);

                insertStmt.setString(1, name);
                insertStmt.setString(2, username);
                insertStmt.setString(3, password);
                insertStmt.setString(4, specialKey); 

                int rowsInserted = insertStmt.executeUpdate();

                out.println("<html><head><title>Registration Status</title></head><body>");
                if (rowsInserted > 0) {
                    out.println("<h1>Registration successful!</h1>");
                    out.println("<p>Name: " + name + "</p>");
                    out.println("<p>Username: " + username + "</p>");
                    out.println("<p>Special Key: " + specialKey + "</p>");
                    out.println("<p><a href='login.html'>Login Now</a></p>");
                } else {
                    out.println("<h1>Registration failed. Please try again.</h1>");
                }
                out.println("</body></html>");
            }

        } catch (ClassNotFoundException e) {
            out.println("<h1>Error: Unable to load database driver</h1>");
            out.println(e.getMessage());
        } catch (SQLException e) {
            out.println("<h1>Database Error:</h1>");
            out.println(e.getMessage());
        } finally {
            
            try { if (rs != null) rs.close(); } catch (SQLException e) {  }
            try { if (checkStmt != null) checkStmt.close(); } catch (SQLException e) { }
            try { if (insertStmt != null) insertStmt.close(); } catch (SQLException e) {  }
            try { if (conn != null) conn.close(); } catch (SQLException e) {  }
        }
    }
}
