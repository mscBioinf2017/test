/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import entities.Users;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author s262012
 */
@Stateless
public class UsersFacade extends AbstractFacade<Users> {

    @PersistenceContext(unitName = "AuthenticAppPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UsersFacade() {
        super(Users.class);
    }
    
    //check if user login exists in DB
    public boolean loginTAken(String login){
        Query q= em.createQuery("SELECT u FROM Users u where u.login=:login");
        q.setParameter("login", login);
        if(q.getResultList().size()>0){
            return true;
        }else{
            return false;
        }
    }
    
    //Method for hashing password using SHA-256
    public String hashPassword(String password){
        try{
            //hashing password
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] hash = sha.digest(password.getBytes("UTF-8"));
            
            //Encoding the hash back into a string 
            StringBuilder hashedPassword = new StringBuilder();
            for(int i=0; i<hash.length;i++){
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length()==1)hashedPassword.append('0');
                hashedPassword.append(hex);   
            }
            return hashedPassword.toString();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(UsersFacade.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(UsersFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    //Registers user with database
    public void registerUser(Users newuser){
        String password = newuser.getPassword();
        newuser.setPassword(hashPassword(password));
        create(newuser);
    }
    
    //Find user by login and unhashed password
    public Users find(String login, String password){
        Query q = em.createQuery("SELECT u FROM Users u WHERE u.login=:login AND u.password=:password");
        q.setParameter("login", login);
        q.setParameter("password", hashPassword(password));
        try {
            return (Users)q.getSingleResult();
        }catch(NoResultException e){
            return null;
        }
    }
}
