package com.baeldung.sendtouser;

import org.springframework.stereotype.Service;

import lombok.var;
import lombok.extern.slf4j.Slf4j;

import java.security.Principal;
import java.util.*;

@Service
@Slf4j
public class ConnectedUserManager {
    public final static int MAX_USER=2;

    private List<String> connectedUser= new ArrayList<>();


    public synchronized void registerNewUser(Principal p){
        connectedUser.add(p.getName());
        log.info("New User::"+p.getName()+" Total Users: "+connectedUser.size());
    }

    public List<String> getConnectedUsers(){
        List<String> l= new ArrayList<>();

        synchronized (connectedUser){
            l.addAll(connectedUser);
        }
        return l;
    }

    /** @return a random logged in user or null if no user logged in
     * 
     */
    public String randomUser() {
        Random r= new Random();
        var users=getConnectedUsers();  
        if (users.size() > 0) {
            return users.get(r.nextInt(users.size()));        
        }else{
            return null;
        }
    }

    public synchronized void deregisterUser(StompPrincipal user) {
        connectedUser.remove(user.getName());
        log.info("Removed User::"+user.getName()+" Total Users: "+connectedUser.size());
    }

    public synchronized boolean isMaxReached() {
        return connectedUser.size() >= MAX_USER;
    }

    public int size() {
        return connectedUser.size();
    }
    
}
