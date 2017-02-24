/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;



import entities.Users;
import facades.UsersFacade;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import javax.servlet.http.HttpSession;

/**
 *
 * @author s262012
 */
@ManagedBean
@SessionScoped
public class AuthentificationBean {

    /**
     * Creates a new instance of AuthentificationBean
     */
    public AuthentificationBean() {
    }
    
    //EJB allowing acces to users entity facade
    @EJB
    UsersFacade usersFacade;
    
    //Entity representing the user being register or logged
    //to be filled out via forms
    private Users newUser = new Users();
    
    public Users getNewUser(){
        return newUser;
    }
    
    public List<Users> getUsers(){
        return usersFacade.findAll();
    }
    
    public String register(){
        String oldPassword = newUser.getPassword();
        
        if(usersFacade.loginTAken(newUser.getLogin())){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("The specific login is already taken."));
            return null;
        }
        usersFacade.registerUser(newUser);
        return logIn(newUser.getLogin(),oldPassword);
    }
    
    public String logIn() {
        return logIn( newUser.getLogin(),  newUser.getPassword());
    }

    public String logIn(String login, String password) {
        Users user = usersFacade.find(login, password);
        if(user!=null){
            HttpSession session = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
            session.setAttribute("user", user);
            return "index";
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("The login or password entered was incorrect."));
        return null;
    }
    
   public String logOut(){
       HttpSession session = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
       session.invalidate();
       return "login";
   }
   
   public String getWelcomeMessage(){
       HttpSession session = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
       Users user = (Users)session.getAttribute("user");
       if(user != null){
           return "Welcome "+user.getFirstname()+" "+user.getLastname()+"!";
       }
       return "Welcome!";
   }
    
}
