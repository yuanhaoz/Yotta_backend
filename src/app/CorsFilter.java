package app;

/**
 * 
 * @author 石磊
 * @date 2016年9月29日20:19:46
 * @description 这是一个过滤器，自己写的，进行了全局的跨域。建议以后改为指定ip可以访问api
 */


import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
 
public class CorsFilter implements ContainerResponseFilter {
 
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
        throws IOException {
 
            responseContext.getHeaders().add("X-Powered-By", "I want you happy!  :-) By shlei");
            responseContext.getHeaders().add("Access-Control-Allow-Origin", Config.IP1);
            responseContext.getHeaders().add("Access-Control-Allow-Headers", "Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma,"
            		+ " Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With");
            responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
            responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
//            responseContext.getHeaders().add("Access-Control-Max-Age", "1209600");
    }
}
