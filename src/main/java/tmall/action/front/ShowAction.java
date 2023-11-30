package tmall.action.front;

import com.opensymphony.xwork2.ActionContext;
import org.apache.struts2.convention.annotation.*;
import tmall.pojo.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Namespace("/")
@ParentPackage("basic-struts")
@Results(
        {
                @Result(name="homePage",location="/home.jsp"),
                @Result(name="categoryPage",location="/category.jsp"),
                @Result(name="searchPage",location="/search.jsp"),
                @Result(name="productPage",location="/product.jsp")
        }
)
@SuppressWarnings("unchecked")

public class ShowAction extends Action4Auth {
    @Action("index")

    public String home(){
        categories = categoryService.list("desc","recommend","max",13);
        // 指定个性筛选规则
        // 1.查找出用户购买过的商品，所在的目录，寻找与目录相关的十个产品
        // 1.1 从Session取出用户ID
        User userSession = (User) ActionContext.getContext().getSession().get("user");
        List<Product> returnProList = new ArrayList<Product>();
        if(null == userSession) {
        	// 没有登陆的前提下，不展示个性化推荐栏位
        	categories.remove(0);
        } else {
        	int id = userSession.getId();
        	User user = new User();
        	user.setId(id);
            // 1.2 查找出所有相关的订单ID
            List<Order> orderList = orderService.list("desc","user","max",10);
            // Set集合本身对目录ID进行去重
            Set<Integer> cIdList = new HashSet<Integer>();

            if(orderList != null && orderList.size() > 0) {
            	
            	for (Order order : orderList) {
            		int oId = order.getId();
            		Order ord = new Order();
            		ord.setId(oId);
            		// 1.3 订单ID查找订单详情得到商品
            		List<OrderItem> orderItems = orderItemService.list("desc","order","max",10);
            		for(OrderItem o : orderItems) {
            			Product pro = o.getProduct();
            			// 1.4 商品ID查找商品表得到目录ID
            			if(null != pro.getCategory()) {
            				int cid = pro.getCategory().getId();
            				cIdList.add(cid);
            			}
            		}
            	}
            	// 1.5 根据cIdList大小，决定每个订单ID取几个商品
            	int size = cIdList.size() == 0 ? 10 : cIdList.size();
            	int num = 10/size;
            	// 1.6 取出10款产品
            	for (int category : cIdList) {
            		Category cat = new Category();
            		cat.setId(category);
    				List<Product> recProList = productService.list("category",cat,"max",num);
    				// 足够的取对应num数量
    				if(recProList != null && recProList.size() > num) {
    					for(int i = 0; i < num; i++) {
    						returnProList.add(recProList.get(i));
    					}
    				} else {
    				// 不够的直接返回全部
    					for(int i = 0; i < recProList.size(); i++) {
    						returnProList.add(recProList.get(i));
    					}
    				}
    			}
            } else {
            	// 2.如果为空，就默认选择销量前十的产品
            	returnProList = productService.list("desc","saleCount","max",10);
            	if(returnProList != null && returnProList.size() >= 10) {
            		returnProList.subList(0, 10);
            	} else {
            		System.out.println("数量不足够，无需操作");
            	}
            }
            // 3.得到十个产品的集合，执行insert
            for (Product product : returnProList) {
            	Category category = new Category();
            	category.setId(9999);
            	product.setCategory(category);
            	//productService.add(product);
    		}
            
            for(Category category:categories){
            	category.setProducts(productService.list("category",category,"stock_gt",0));
            	System.out.println(category.getProducts());
            }
            // 删除十个产品，以便每次的十条数据都是最新的推荐信息
            for (Product product : returnProList) {
            	//productService.delete(product);
            }
            return "homePage";
        }
        for(Category category:categories){
        	category.setProducts(productService.list("category",category,"stock_gt",0));
        	System.out.println(category.getProducts());
        }
        return "homePage";
    }

    @Action("product")
    public String product(){
        fill(product);
        productTopImages = productImageService.list("product",product,"type", ProductImage.Type.top);
        productDetailImages = productImageService.list("product",product,"type", ProductImage.Type.detail);
        comments = commentService.list("product",product);
        propertyValues = propertyValueService.list("product",product);
        return "productPage";
    }
    
    @Action("category")
    public String category(){
        fill(category);
        products = productService.list("category",category,handleSort()[0],handleSort()[1],"stock_gt",0);
        return "categoryPage";
    }
    @Action("search")
    public String search(){
        products = productService.list("name_like",keyword,handleSort()[0],handleSort()[1],"stock_gt",0);
        return "searchPage";
    }
}