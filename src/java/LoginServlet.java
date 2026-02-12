import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class LoginServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {

        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            
            Class.forName("com.mysql.jdbc.Driver");

           
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/harsh", "root", "12345678");

           
            String selectQuery = "SELECT * FROM users WHERE username = ? AND password = ?";
            st = conn.prepareStatement(selectQuery);
            st.setString(1, username);
            st.setString(2, password);

            rs = st.executeQuery();

            if (rs.next()) {
                
                Cookie userCookie = new Cookie("username", username);
                userCookie.setMaxAge(60 * 60 * 24);
                res.addCookie(userCookie);

                
                out.println("<html><head><title>Redirecting</title></head><body>");
                out.println("<form action='MainServlet' method='post' id='redirectForm'>");
                out.println("<input type='hidden' name='username' value='" + username + "'>");
                out.println("</form>");
                out.println("<script>document.getElementById('redirectForm').submit();</script>");
                out.println("</body></html>");
            } else {
                
                out.println("<h3 style='color:red;'>Invalid username or password. Please try again.</h3>");
                RequestDispatcher rd = req.getRequestDispatcher("index.html");
                rd.include(req, res);
            }

        } catch (ClassNotFoundException e) {
            out.println("<h1>Error: Unable to load database driver</h1>");
            out.println(e.getMessage());
        } catch (SQLException e) {
            out.println("<h1>Database Error:</h1>");
            out.println(e.getMessage());
        } finally {
           
            try { if (conn != null) conn.close(); } catch (SQLException e) {  }
            try { if (st != null) st.close(); } catch (SQLException e) { }
        }
    }
} 