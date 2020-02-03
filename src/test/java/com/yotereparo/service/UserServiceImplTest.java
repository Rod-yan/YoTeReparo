package com.yotereparo.service;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
 
import java.util.ArrayList;
import java.util.List;
 
import static org.mockito.Mockito.when;
 
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.yotereparo.dao.UserDaoImpl;
import com.yotereparo.model.Address;
import com.yotereparo.model.City;
import com.yotereparo.model.Role;
import com.yotereparo.model.User;

public class UserServiceImplTest {
	
	@Mock
    UserDaoImpl dao;
	
	@Mock
    CityServiceImpl cityService;
	
	@Mock
    MessageSource message;
	
	@Mock
	RoleServiceImpl roleService;
     
    @InjectMocks
    UserServiceImpl userService;
    
    @Mock
    Environment environment;
     
    @Spy
    List<User> users = new ArrayList<User>();
    
    @Spy
    Address address = new Address();
    
    @Spy
    City city = new City();
     
    @BeforeClass
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        
        address.setCalle("TestCalle");
        address.setAltura(123);
        address.setDepartamento("TestDepto");
        address.setId(10000);
        address.setPiso("4");
        
        city.setId("venado_tuerto");
        city.setDescripcion("Venado Tuerto");
        
        users = getUsersList();
    }
 
    @Test
    public void getUserById(){
        User user = users.get(0);
        when(dao.getUserById(anyString())).thenReturn(user);
        Assert.assertEquals(userService.getUserById(user.getId()), user);
    }
 
    @Test
    public void createUser(){
    	User user = users.get(0);
    	Role role = new Role();
    	role.setId("USUARIO_FINAL");
    	role.setDescripcion("Usuario Final");
    	
        doNothing().when(dao).createUser(any(User.class));
        when(environment.getProperty(anyString())).thenReturn("180");
        when(roleService.getRoleById(anyString())).thenReturn(role);
        userService.createUser(user);
        verify(dao, atLeastOnce()).createUser(any(User.class));
    }
     
    @Test
    public void updateUser(){
        User user = users.get(0);
        when(dao.getUserById(anyString())).thenReturn(user);
        userService.updateUser(user);
        verify(dao, atLeastOnce()).getUserById(anyString());
    }
 
    @Test
    public void deleteUserById(){
        doNothing().when(dao).deleteUserById(anyString());
        userService.deleteUserById(anyString());
        verify(dao, atLeastOnce()).deleteUserById(anyString());
    }
     
    @Test
    public void getAllUsers(){
        when(dao.getAllUsers()).thenReturn(users);
        Assert.assertEquals(userService.getAllUsers(), users);
    }
     
    @Test
    public void exist(){
        User user = users.get(0);
        when(dao.getUserById(anyString())).thenReturn(user);
        Assert.assertEquals(userService.exist(anyString()), true);
    }
 
    @Test
    public void hasUniqueId(){
        User user = users.get(0);
        when(userService.getUserById(anyString())).thenReturn(null);
        Assert.assertEquals(userService.hasUniqueId(user.getId()), true);
    }
     
     
    public List<User> getUsersList(){
    	User testUser1 = new User();
        testUser1.setId("testUser7");
        testUser1.setNombre("Test");
        testUser1.setApellido("User7");
        testUser1.setEmail("testuser7@yotereparo.com");
        testUser1.setContrasena("test2001");
        testUser1.setCiudad(city);
        testUser1.setMembresia(null);
        testUser1.setEstado("TST");
        testUser1.setIntentosIngreso(0);
        testUser1.getDirecciones().add(address);
         
        User testUser2 = new User();
        testUser2.setId("testUser8");
        testUser2.setNombre("Test");
        testUser2.setApellido("User8");
        testUser2.setEmail("testuser2@yotereparo.com");
        testUser2.setContrasena("test2002");
        testUser2.setCiudad(city);
        testUser2.setMembresia(null);
        testUser2.setEstado("TST");
        testUser2.setIntentosIngreso(0);
        testUser2.getDirecciones().add(address);
         
        users.add(testUser1);
        users.add(testUser2);
        return users;
    }

}
