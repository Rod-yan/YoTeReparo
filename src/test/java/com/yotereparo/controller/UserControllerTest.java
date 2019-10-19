package com.yotereparo.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
 
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import static org.mockito.Mockito.atLeastOnce;
 
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.util.UriComponentsBuilder;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.yotereparo.model.User;
import com.yotereparo.service.UserServiceImpl;
import com.yotereparo.util.SecurityUtils;
 
public class UserControllerTest {
 
    @Mock
    UserServiceImpl userService;
     
    @Mock
    MessageSource message;
     
    @InjectMocks
    UserController userController;
     
    @Spy
    List<User> users = new ArrayList<User>();
     
    @Mock
    BindingResult result;
    
    @Spy
    UriComponentsBuilder ucBuilder;
     
    @BeforeClass
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        users = getUsersList();
    }
     
    @Test
    public void getAllUsers(){
    	ResponseEntity<?> responseEntity = new ResponseEntity<>(
    			users, 
	            HttpStatus.OK
	        );
    	
        when(userService.getAllUsers()).thenReturn(users);
        Assert.assertEquals(userController.listUsers(), responseEntity);
        verify(userService, atLeastOnce()).getAllUsers();
    }
    
    @Test
    public void getUser_WithValidationErrorDoesntExist(){
        when(userService.getUserById(anyString())).thenReturn(null);
        Assert.assertEquals(userController.getUser(anyString()).getStatusCode(), HttpStatus.NOT_FOUND);
    }
    @Test
    public void getUser_Success(){
    	User user = users.get(0);
    	ResponseEntity<?> responseEntity = new ResponseEntity<>(
    			user, 
	            HttpStatus.OK
	        );
    	
        when(userService.getUserById(anyString())).thenReturn(user);
        Assert.assertEquals(userController.getUser(anyString()), responseEntity);
    }
    
    @Test
    public void createUser_WithInputValidationError(){
    	when(result.hasErrors()).thenReturn(true);
        when(userService.exist(anyString())).thenReturn(false);
        doNothing().when(userService).createUser(any(User.class));
        Assert.assertEquals(userController.createUser(users.get(0), ucBuilder, result).getStatusCode(), HttpStatus.BAD_REQUEST);
    }
 
    @Test
    public void createUser_WithValidationErrorAlreadyExist(){
    	when(result.hasErrors()).thenReturn(false);
        when(userService.exist(anyString())).thenReturn(true);
        Assert.assertEquals(userController.createUser(users.get(0), ucBuilder, result).getStatusCode(), HttpStatus.CONFLICT);
    }
 
    @Test
    public void createUser_Success(){
        when(result.hasErrors()).thenReturn(false);
        when(userService.exist(anyString())).thenReturn(false);
        doNothing().when(userService).createUser(any(User.class));
        Assert.assertEquals(userController.createUser(users.get(0), ucBuilder, result).getStatusCode(), HttpStatus.CREATED);
    }

    @Test
    public void updateUser_WithInputValidationError(){
    	when(result.hasErrors()).thenReturn(true);
        when(userService.exist(anyString())).thenReturn(true);
        doNothing().when(userService).updateUser(any(User.class));
        Assert.assertEquals(userController.updateUser(users.get(0).getId(), users.get(0), result).getStatusCode(), HttpStatus.BAD_REQUEST);
    }
    
    @Test
    public void updateUser_WithValidationErrorDoesntExist(){
    	when(result.hasErrors()).thenReturn(false);
        when(userService.exist(anyString())).thenReturn(false);
        Assert.assertEquals(userController.updateUser(users.get(0).getId(), users.get(0), result).getStatusCode(), HttpStatus.NOT_FOUND);
    }
 
    @Test
    public void updateUser_Success(){
    	User user = users.get(0);
    	ResponseEntity<?> responseEntity = new ResponseEntity<>(
    			user, 
	            HttpStatus.OK
	        );
    	
        when(result.hasErrors()).thenReturn(false);
        when(userService.exist(anyString())).thenReturn(true);
        when(userService.getUserById(anyString())).thenReturn(user);
        doNothing().when(userService).updateUser(any(User.class));
        Assert.assertEquals(userController.updateUser(users.get(0).getId(), users.get(0), result), responseEntity);
    }
    
    @Test
    public void deleteUser_WithValidationErrorDoesntExist(){
    	when(result.hasErrors()).thenReturn(false);
        when(userService.exist(anyString())).thenReturn(false);
        Assert.assertEquals(userController.deleteUser(anyString()).getStatusCode(), HttpStatus.NOT_FOUND);
    }
     
    @Test
    public void deleteUser_Success(){
    	when(userService.exist(anyString())).thenReturn(true);
        doNothing().when(userService).deleteUserById(anyString());
        Assert.assertEquals(userController.deleteUser(anyString()).getStatusCode(), HttpStatus.OK);
    }
 
    public List<User> getUsersList(){
        User testUser1 = new User();
        testUser1.setId("testUser7");
        testUser1.setNombre("Test");
        testUser1.setApellido("User7");
        testUser1.setEmail("testuser7@yotereparo.com");
        testUser1.setContrasena("test2001");
        testUser1.setSalt(SecurityUtils.saltGenerator());
        testUser1.hashContrasena();
        testUser1.setEstado("TEST");
        testUser1.setMembresia("TEST");
        testUser1.setIntentosIngreso(0);
         
        User testUser2 = new User();
        testUser2.setId("testUser8");
        testUser2.setNombre("Test");
        testUser2.setApellido("User8");
        testUser2.setEmail("testuser2@yotereparo.com");
        testUser2.setContrasena("test2002");
        testUser2.setSalt(SecurityUtils.saltGenerator());
        testUser2.hashContrasena();
        testUser2.setEstado("TEST");
        testUser2.setMembresia("TEST");
        testUser2.setIntentosIngreso(0);
         
        users.add(testUser1);
        users.add(testUser2);
        return users;
    }
}