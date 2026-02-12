import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class ForgotPasswordServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

       
        String username = req.getParameter("username");
        String specialKey = req.getParameter("special_key");
        String newPassword = req.getParameter("new_password");

        Connection conn = null;
        PreparedStatement checkStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;

        try {
            
            Class.forName("com.mysql.jdbc.Driver");

           
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/harsh", "root", "12345678");

            
            String checkQuery = "SELECT * FROM users WHERE username = ? AND sp_key = ?";
            checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, username);
            checkStmt.setString(2, specialKey);
            rs = checkStmt.executeQuery();

            if (rs.next()) {
                
                String updateQuery = "UPDATE users SET password = ? WHERE username = ?";
                updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setString(1, newPassword);
                updateStmt.setString(2, username);

                int rowsUpdated = updateStmt.executeUpdate();

                out.println("<html><head><title>Password Update Status</title></head><body>");
                if (rowsUpdated > 0) {
                    out.println("<h1>Password updated successfully!</h1>");
                    out.println("<p><a href='index.html'>Go to Login</a></p>");
                } else {
                    out.println("<h1>Failed to update password. Please try again.</h1>");
                }
                out.println("</body></html>");
            } else {
                
                out.println("<html><head><title>Error</title></head><body>");
                out.println("<h1 style='color:red;'>Invalid username or special key. Please try again.</h1>");
                out.println("<p><a href='forgotPassword.html'>Try again</a></p>");
                out.println("</body></html>");
            }

        } catch (ClassNotFoundException e) {
            out.println("<h1>Error: Unable to load database driver</h1>");
            out.println(e.getMessage());
        } catch (SQLException e) {
            out.println("<h1>Database Error:</h1>");
            out.println(e.getMessage());
        } finally {
            
            try { if (rs != null) rs.close(); } catch (SQLException e) { }
            try { if (checkStmt != null) checkStmt.close(); } catch (SQLException e) {  }
            try { if (updateStmt != null) updateStmt.close(); } catch (SQLException e) { }
            try { if (conn != null) conn.close(); } catch (SQLException e) { }
        }
    }
}
