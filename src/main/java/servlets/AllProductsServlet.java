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

public class AllProductsServlet extends HttpServlet {
    private SecurityService securityService = new SecurityService();
    private JdbcProductDao jdbcProductDao = new JdbcProductDao();
    private List<String> userTokens;

    public AllProductsServlet(List<String> userTokens) {
        this.userTokens = userTokens;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        boolean isAuth = securityService.isAuth(request, userTokens);
        if (isAuth) {
            Map<String, Object> paramMap = new HashMap<>();

            List<Product> products;
            try {
                products = jdbcProductDao.getAllProducts();

                paramMap.put("products", products);

                PageGenerator pageGenerator = PageGenerator.instance();
                String page = pageGenerator.getPage("allproducts.html", paramMap);

                response.setContentType("text/html;charset=utf-8");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(page);
            } catch (IOException exception) {
                throw new RuntimeException("Cant show all products from database");
            }
        } else {
            try {
                response.sendRedirect("/login");
            } catch (IOException exception) {
                throw new RuntimeException("You have to log in");
            }
        }
    }
}