package br.com.fiap.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;


public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        // URLs que não precisam de autenticação
        boolean isPublicPage = path.equals("/login") ||
                path.equals("/login.jsp") ||
                path.equals("/cadastro") ||
                path.equals("/cadastrar") ||
                path.equals("/cadastro.jsp") ||
                path.equals("/CadastroServlet") ||
                path.startsWith("/resources/") ||
                path.endsWith(".css") ||
                path.endsWith(".js") ||
                path.endsWith(".ico");

        if (isPublicPage) {
            // Permite acesso a recursos públicos
            chain.doFilter(request, response);
            return;
        }

        // Verifica se usuário está logado
        boolean isLoggedIn = (session != null && session.getAttribute("usuarioLogado") != null);

        if (isLoggedIn) {
            chain.doFilter(request, response);
        } else {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp");
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}