package servlets;

import dao.JdbcProductDao;
import entity.Product;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pagegenerator.PageGenerator;
import security.SecurityService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddProductServlet extends HttpServlet {
    private List<String> userTokens;
    private JdbcProductDao jdbcProductDao = new JdbcProductDao();
    private SecurityService securityService = new SecurityService();

    public AddProductServlet(List<String> userTokens) {
        this.userTokens = userTokens;
    }

    public AddProductServlet() {
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> pageVariables = createPageVariablesMap(request);
        PageGenerator pageGenerator = PageGenerator.instance();
        String page = pageGenerator.getPage("addproduct.html", pageVariables);
        try {
            response.getWriter().println(page);
        } catch (IOException exception) {
            throw new RuntimeException("Cant get data from request");
        }
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        int id = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        int price = Integer.parseInt(request.getParameter("price"));
        String creationdate = request.getParameter("creationdate");
        Product product = new Product(id, name, price, creationdate);
        response.setContentType("text/html;charset=utf-8");
        try {
            jdbcProductDao.save(product);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Check new product`s parameters. Cant add to database");
        }
        try {
            response.sendRedirect("/products");
        } catch (IOException e) {
            throw new RuntimeException("Cant show table with update products");
        }
        try {
            response.getWriter().close();
        } catch (IOException exception) {
            throw new RuntimeException("Cant show update products");
        }
    }

    public static Map<String, Object> createPageVariablesMap(HttpServletRequest request) {
        Map<String, Object> pageVariables = new HashMap<>();
        pageVariables.put("id", request.getParameter("id"));
        pageVariables.put("name", request.getParameter("name"));
        pageVariables.put("price", request.getParameter("price"));
        pageVariables.put("creationdate", request.getParameter("creationdate"));
        return pageVariables;
    }
}
