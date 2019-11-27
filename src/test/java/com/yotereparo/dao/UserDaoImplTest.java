package com.yotereparo.dao;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.yotereparo.model.City;
import com.yotereparo.model.User;
import com.yotereparo.util.SecurityUtils;
 
 
public class UserDaoImplTest extends EntityDaoImplTest {
 
    @Autowired
    UserDao userDao;
    
    // Definimos un dataset in-memory
    @Override
    protected IDataSet getDataSet() throws Exception{
        IDataSet dataSet = new FlatXmlDataSet(this.getClass().getClassLoader().getResourceAsStream("DataSet.xml"));
        return dataSet;
    }
 
    @Test
    public void getUserById(){
        Assert.assertNotNull(userDao.getUserById("testUser1"));
        Assert.assertNull(userDao.getUserById("testUser3"));
    }
 
    @Test
    public void createUser(){
        userDao.createUser(getSampleUser());
        Assert.assertEquals(userDao.getAllUsers().size(), 3);
    }
     
    @Test
    public void deleteUserById(){
        userDao.deleteUserById("testUser1");
        Assert.assertEquals(userDao.getAllUsers().size(), 1);
    }
     
    @Test
    public void deleteUserByInvalidId(){
        userDao.deleteUserById("testUser3");
        Assert.assertEquals(userDao.getAllUsers().size(), 2);
    }
 
    @Test
    public void getAllUsers(){
        Assert.assertEquals(userDao.getAllUsers().size(), 2);
    }
 
    public User getSampleUser(){
    	City city = new City();
    	city.setId("venado_tuerto");
    	city.setDescripcion("Venado Tuerto");
    	city.setProvincia("santa_fe");
    	
        User user = new User();
        user.setId("testUser0");
        user.setNombre("Test");
        user.setApellido("User0");
        user.setEmail("testuser0@yotereparo.com");
        user.setContrasena("test2000");
        user.setSalt(SecurityUtils.saltGenerator());
        user.setFechaNacimiento(new LocalDate());
        user.setTelefonoPrincipal("123123123123");
        user.setTelefonoAlternativo("123123123123");
        user.setCiudad(city);
        user.setDescripcion("test user description");
        user.setFechaExpiracionContrasena(new DateTime().plusDays(180));
        user.setFechaUltimoCambioContrasena(new DateTime());
        user.setFechaUltimoIngreso(new DateTime());
		user.setFechaCreacion(new DateTime());
        user.setEstado("TEST");
        user.setMembresia(null);
        user.setIntentosIngreso(0);
        user.setRoles(null);
        return user;
    }
 
}