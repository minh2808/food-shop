package com.ecom.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ecom.exception.ResourceNotFoundException;
import com.ecom.exception.InsufficientStockException;

import com.ecom.model.Cart;
import com.ecom.model.Category;
import com.ecom.model.OrderRequest;
import com.ecom.model.Product;
import com.ecom.model.ProductOrder;
import com.ecom.model.UserDtls;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.OrderService;
import com.ecom.service.UserService;
import com.ecom.service.ProductService;
import com.ecom.util.OrderStatus;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {


    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CartService cartService;


    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String home() {
        return "user/home";
    }

    @ModelAttribute
    public void getUserDetails(Principal p, Model m) {
        if (p != null) {
            String email = p.getName();
            UserDtls userDtls = userService.getUserByEmail(email);
            m.addAttribute("user", userDtls);
            Integer countCart = cartService.getCountCart(userDtls.getId());
            m.addAttribute("countCart", countCart);
        }
        List<Category> allCategory = categoryService.getAllCategory();
        m.addAttribute("categorys", allCategory);
    }


    // Thông báo thêm vào Giỏ hàng
    @GetMapping("/addCart")
    public String addToCart(@RequestParam Integer pid, @RequestParam Integer uid, HttpSession session) {
        try {
            Cart saveCart = cartService.saveCart(pid, uid);
            if (!ObjectUtils.isEmpty(saveCart)) {
                session.setAttribute("succMsg", "Sản phẩm được thêm vào giỏ hàng thành công");
                return "redirect:/user/cart";   
            }

        } catch (ResourceNotFoundException e) {
            session.setAttribute("errorMsg", "Không tìm thấy sản phẩm hoặc người dùng");

        } catch (InsufficientStockException e) {
            session.setAttribute("errorMsg", "Trong kho không đủ sản phẩm, không thể đặt");

        } catch (Exception e) {
            session.setAttribute("errorMsg", "Đã xảy ra lỗi hệ thống không mong muốn.");
        }
        return "redirect:/viewProduct/" + pid;         // Đặt hàng không thành chuyển về trang sản phẩm
    }

    @GetMapping("/viewProduct/{id}")
        public String product(@PathVariable int id, Model m, 
                            HttpSession session, 
                            RedirectAttributes redirectAttributes) { // Thêm RedirectAttributes
            try {
                Product productById = productService.getProductById(id);
                m.addAttribute("product", productById);
            } catch (ResourceNotFoundException e) {
                // CHUYỂN SANG DÙNG FLASH ATTRIBUTES và REDIRECT
                redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: Không tìm thấy sản phẩm này!");
                return "redirect:/products"; // Chuyển hướng về trang danh sách sản phẩm
                
            } catch (InsufficientStockException e) {
                redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: Trong kho không đủ sản phẩm.");
                return "redirect:/products"; 
                
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMsg", "Đã xảy ra lỗi hệ thống không mong muốn.");
                return "redirect:/products";
            }
            return "view_product";
        }
    // Trang Cart
    @GetMapping("/cart")
    public String loadCartPage(Principal p, Model m, HttpSession session) {
        UserDtls user = getLoggedInUserDetails(p);
        try {
            List<Cart> carts = cartService.getCartsByUser(user.getId());
            m.addAttribute("carts", carts);
            if (carts.size() > 0) {
                // Lấy tổng giá cuối cùng
                Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
                m.addAttribute("totalOrderPrice", totalOrderPrice);

            } else {
                // Giỏ hàng trống, tổng giá bằng 0
                m.addAttribute("totalOrderPrice", 0.0);
            }
            return "/user/cart";

        } catch (ResourceNotFoundException e) {
            session.setAttribute("errorMsg", "Lỗi: " + e.getMessage());
            return "redirect:/user/";
        } catch (Exception e) {
            session.setAttribute("errorMsg", "Đã xảy ra lỗi không mong muốn khi tải giỏ hàng.");
            return "redirect:/user/";
        }
    }


    // THAY THẾ PHƯƠNG THỨC CART QUANTITY UPDATE
    @GetMapping("/cartQuantityUpdate")
    public String updateCartQuantity(
            @RequestParam(required = false) String sy,           // sy: de, in, hoặc set
            @RequestParam Integer cid,                             // cid: Cart ID
            @RequestParam(required = false) Integer newQuantity,  // newQuantity: Số lượng mới từ input
            RedirectAttributes redirectAttributes) {
        
        try {
            if ("set".equalsIgnoreCase(sy) && newQuantity != null) {
                // Cập nhật trực tiếp từ ô nhập liệu (sy=set)
                cartService.updateQuantityByInput(cid, newQuantity);
                redirectAttributes.addFlashAttribute("succMsg", "Cập nhật số lượng thành công!");
            
            } else if ("in".equalsIgnoreCase(sy) || "de".equalsIgnoreCase(sy)) {
                 // Cập nhật tăng/giảm (từ nút + / -)
                 cartService.updateQuantity(sy, cid);
                 redirectAttributes.addFlashAttribute("succMsg", "Cập nhật giỏ hàng thành công!");

            } else {
                 // Nhập sai
                 if (newQuantity != null) {
                    cartService.updateQuantityByInput(cid, newQuantity);
                    redirectAttributes.addFlashAttribute("succMsg", "Cập nhật số lượng thành công!");
                 } else {
                    redirectAttributes.addFlashAttribute("errorMsg", "Nhập lại.");
                 }
            }
            
        } catch (InsufficientStockException e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi tồn kho: " + e.getMessage());
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: Không tìm thấy giỏ hàng.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Đã xảy ra lỗi không mong muốn khi cập nhật giỏ hàng.");
        }

        return "redirect:/user/cart";
    }


    private UserDtls getLoggedInUserDetails(Principal p) {
        String email = p.getName();
        UserDtls userDtls = userService.getUserByEmail(email);

        if (userDtls == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng đã đăng nhập có email là: " + email);
        }
        return userDtls;
    }


    @GetMapping("/orders")
    public String orderPage(Principal p, Model m, HttpSession session) {
        UserDtls user = getLoggedInUserDetails(p);
        try {
            List<Cart> carts = cartService.getCartsByUser(user.getId());
            m.addAttribute("carts", carts);
            if (carts.size() > 0) {
                Integer quantity = carts.get(carts.size() - 1).getQuantity();
                Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
                m.addAttribute("quantity", quantity);
                m.addAttribute("totalOrderPrice", totalOrderPrice);
            }
            return "/user/order";

        } catch (ResourceNotFoundException e) {
            session.setAttribute("errorMsg", "Lỗi: Người dùng không tồn tại hoặc tài nguyên liên quan không tìm thấy.");
            return "redirect:/user/";

        } catch (Exception e) {
            session.setAttribute("errorMsg", "Đã xảy ra lỗi không mong muốn khi tải trang đặt hàng.");
            return "redirect:/user/cart";
        }
    }


    @PostMapping("/save-order")
    public String saveOrder(@ModelAttribute OrderRequest request, Principal p, RedirectAttributes redirectAttributes) {
        UserDtls user = getLoggedInUserDetails(p);

        try {
            orderService.saveOrder(user.getId(), request);
            return "redirect:/user/success";

        } catch (ResourceNotFoundException | InsufficientStockException e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi đặt hàng: " + e.getMessage());
            return "redirect:/user/orders";

        } catch (Exception e) {
            // include exception message to help debug transaction errors
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi giao dịch không mong muốn: " + e.getMessage());
            return "redirect:/user/orders";
        }
    }

    @GetMapping("/success")
    public String loadSuccess() {

        return "/user/success";
    }

    @GetMapping("/user-orders")
    public String myOrder(Model m, Principal p, HttpSession session) {
        try {
            UserDtls loginUser = getLoggedInUserDetails(p); // Hàm này có thể ném lỗi
            List<ProductOrder> orders = orderService.getOrdersByUser(loginUser.getId());
            m.addAttribute("orders", orders);
            return "/user/my_orders";

        } catch (ResourceNotFoundException e) {
            session.setAttribute("errorMsg", "Lỗi truy cập: Tài khoản không hợp lệ.");
            return "redirect:/user/";

        } catch (Exception e) {
            session.setAttribute("errorMsg", "Đã xảy ra lỗi khi tải lịch sử đơn hàng.");
            return "redirect:/user/";
        }
    }

    @GetMapping("/update-status")
    public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) {
        OrderStatus[] values = OrderStatus.values();
        String status = null;
        for (OrderStatus orderSt : values) {
            if (orderSt.getId().equals(st)) {
                status = orderSt.getName();
                break;
            }
        }

        try {
            if (status == null) {
                session.setAttribute("error", "Mã trạng thái không hợp lệ.");
                return "redirect:/user/user-orders";
            }
            // If cancelling, restore stock and delete the order
            if (OrderStatus.CANCEL.getName().equals(status)) {
                orderService.cancelOrder(id);
                session.setAttribute("succMsg", "Đã hủy đơn hàng và hoàn trả kho.");
            } else {
                ProductOrder updateOrder = orderService.updateOrderStatus(id, status);
                if (!ObjectUtils.isEmpty(updateOrder)) {
                    session.setAttribute("succMsg", "Cập nhật thành công");
                } else {
                    session.setAttribute("errorMsg", "Cập nhật thất bại. Đơn hàng không tìm thấy hoặc lỗi nội bộ.");
                }
            }

        } catch (ResourceNotFoundException e) {
            session.setAttribute("errorMsg", "Lỗi: Không tìm thấy đơn hàng cần cập nhật.");

        } catch (Exception e) {
            session.setAttribute("errorMsg", "Lỗi hệ thống khi cập nhật trạng thái.");
        }

        return "redirect:/user/user-orders";
    }

    // Xóa thư mục
    @GetMapping("/removeCart/{id}")
    public String removeCart(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            cartService.removeCartItem(id);
            redirectAttributes.addFlashAttribute("succMsg", "Đã xóa mục khỏi giỏ hàng thành công!");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: Không tìm thấy mục cần xóa.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Đã xảy ra lỗi khi xóa mục giỏ hàng.");
        }
        return "redirect:/user/cart";
    }
}