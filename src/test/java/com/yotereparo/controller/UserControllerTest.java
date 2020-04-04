package com.yotereparo.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;

import static org.mockito.Mockito.atLeastOnce;
 
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yotereparo.controller.dto.UserDto;
import com.yotereparo.model.Address;
import com.yotereparo.model.City;
import com.yotereparo.model.User;
import com.yotereparo.service.CityServiceImpl;
import com.yotereparo.service.UserServiceImpl;
 
public class UserControllerTest {
 
    @Mock
    UserServiceImpl userService;
    
    @Mock
    CityServiceImpl cityService;
    
    @Mock
    ModelMapper modelMapper;
     
    @Mock
    MessageSource message;
     
    @InjectMocks
    UserController userController;
     
    @Spy
    List<User> users = new ArrayList<User>();
    
    @Spy
    List<UserDto> usersDto = new ArrayList<UserDto>();
    
    @Spy
    Address address = new Address();
    
    @Spy
    City city = new City();
     
    @Mock
    BindingResult result;
    
    @Spy
    UriComponentsBuilder ucBuilder;
     
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
        usersDto = getUsersDtoList();
    }
     
    @Test
    public void getAllUsers(){
    	User user0 = users.get(0);
    	User user1 = users.get(1);
    	UserDto userDto0 = usersDto.get(0);
    	UserDto userDto1 = usersDto.get(1);
    	ResponseEntity<List<UserDto>> responseEntity = new ResponseEntity<List<UserDto>>(
    			(List<UserDto>) usersDto, 
	            HttpStatus.OK
	        );
    	
        when(userService.getAllUsers()).thenReturn(users);
        when(modelMapper.map(user0, UserDto.class)).thenReturn(userDto0);
        when(modelMapper.map(user1, UserDto.class)).thenReturn(userDto1);
        when(cityService.getCityById(anyString())).thenReturn(city);
        Assert.assertEquals(userController.listUsers().toString(), responseEntity.toString());
        verify(userService, atLeastOnce()).getAllUsers();
    }
    
    @Test
    public void getUser_WithValidationDoesntExist_InputErrors(){
        when(userService.getUserById(anyString())).thenReturn(null);
        Assert.assertEquals(userController.getUser(anyString()).getStatusCode(), HttpStatus.NOT_FOUND);
    }
    
    @Test
    public void getUser_Success(){
    	User user = users.get(0);
    	UserDto userDto = usersDto.get(0);
    	ResponseEntity<?> responseEntity = new ResponseEntity<>(
    			userDto, 
	            HttpStatus.OK
	        );
    	
        when(userService.getUserById(anyString())).thenReturn(user);
        when(modelMapper.map(any(User.class), eq(UserDto.class))).thenReturn(userDto);
        when(cityService.getCityById(anyString())).thenReturn(city);
        Assert.assertEquals(userController.getUser(anyString()), responseEntity);
    }
    
    @Test
    public void createUser_WithValidationError_InputErrors(){
    	when(result.hasErrors()).thenReturn(true);
        when(userService.getUserById(anyString())).thenReturn(null);
        when(cityService.getCityById(anyString())).thenReturn(city);
        doNothing().when(userService).createUser(any(User.class));
        Assert.assertEquals(userController.createUser(usersDto.get(0), ucBuilder, result).getStatusCode(), HttpStatus.BAD_REQUEST);
    }
 
    @Test
    public void createUser_WithValidationError_UserAlreadyExist(){
    	when(result.hasErrors()).thenReturn(false);
    	when(userService.getUserById(anyString())).thenReturn(any(User.class));
        when(cityService.getCityById(anyString())).thenReturn(city);
        Assert.assertEquals(userController.createUser(usersDto.get(0), ucBuilder, result).getStatusCode(), HttpStatus.CONFLICT);
    }
 
    @Test
    public void createUser_Success(){
    	UserDto userDto = usersDto.get(0);
    	User user = users.get(0);
        when(result.hasErrors()).thenReturn(false);
        when(userService.getUserById(anyString())).thenReturn(null);
        when(modelMapper.map(any(UserDto.class), eq(User.class))).thenReturn(user);
        when(cityService.getCityById(anyString())).thenReturn(any(City.class));
        when(cityService.getCityById(anyString())).thenReturn(city);
        doNothing().when(userService).createUser(any(User.class));
        Assert.assertEquals(userController.createUser(userDto, ucBuilder, result).getStatusCode(), HttpStatus.CREATED);
    }

    @Test
    public void updateUser_WithValidationError_InputErrors(){
    	when(result.hasErrors()).thenReturn(true);
        when(userService.getUserById(anyString())).thenReturn(any(User.class));
        when(cityService.getCityById(anyString())).thenReturn(city);
        doNothing().when(userService).updateUser(any(User.class));
        Assert.assertEquals(userController.updateUser(usersDto.get(0).getId(), usersDto.get(0), result).getStatusCode(), HttpStatus.BAD_REQUEST);
    }
    
    @Test
    public void updateUser_WithValidationError_UserDoesntExist(){
    	when(result.hasErrors()).thenReturn(false);
        when(userService.getUserById(anyString())).thenReturn(null);
        Assert.assertEquals(userController.updateUser(usersDto.get(0).getId(), usersDto.get(0), result).getStatusCode(), HttpStatus.NOT_FOUND);
    }
 
    @Test
    public void updateUser_Success(){
    	User user = users.get(0);
    	ResponseEntity<?> responseEntity = new ResponseEntity<>(
    			usersDto.get(0), 
	            HttpStatus.OK
	        );
    	
        when(result.hasErrors()).thenReturn(false);
        when(userService.getUserById(anyString())).thenReturn(any(User.class));
        when(userService.getUserById(anyString())).thenReturn(user);
        when(cityService.getCityById(anyString())).thenReturn(city);
        doNothing().when(userService).updateUser(any(User.class));
        Assert.assertEquals(userController.updateUser(usersDto.get(0).getId(), usersDto.get(0), result), responseEntity);
    }
    
    @Test
    public void deleteUser_WithValidationError_UserDoesntExist(){
    	when(result.hasErrors()).thenReturn(false);
        when(userService.getUserById(anyString())).thenReturn(null);
        Assert.assertEquals(userController.deleteUser(anyString()).getStatusCode(), HttpStatus.NOT_FOUND);
    }
     
    @Test
    public void deleteUser_Success(){
    	when(userService.getUserById(anyString())).thenReturn(any(User.class));
        doNothing().when(userService).deleteUserById(anyString());
        Assert.assertEquals(userController.deleteUser(anyString()).getStatusCode(), HttpStatus.OK);
    }
    
    @Test
    public void getUserPhoto_WithValidationError_UserDoesntExist(){
        when(userService.getUserById(anyString())).thenReturn(null);
        Assert.assertEquals(userController.getUserPhoto(anyString()).getStatusCode(), HttpStatus.NOT_FOUND);
    }
    
    @Test
    public void getUserPhoto_WithValidationError_UserDoesntHavePhoto(){
    	User user = users.get(0);
    	
    	MockHttpServletRequest request = new MockHttpServletRequest();
    	request.setRequestURI("/yotereparo/./photo");
    	RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    	
        when(userService.getUserById(anyString())).thenReturn(any(User.class));
        when(userService.getUserById(anyString())).thenReturn(user);
        Assert.assertEquals(userController.getUserPhoto(anyString()).getStatusCode(), HttpStatus.NOT_FOUND);
    }
    
    @Test
    public void getUserPhoto_Success() throws IOException{
    	User user = new User();
    	user.setId("testUserWithPhoto");
    	ByteArrayOutputStream os = new ByteArrayOutputStream();
    	BufferedImage photo = ImageIO.read(getClass().getClassLoader().getResource("tstimage.png"));
    	ImageIO.write(photo, "png", os);
    	user.setFoto(os.toByteArray());
    	photo.flush();
    	os.close();
    	
    	MockHttpServletRequest request = new MockHttpServletRequest();
    	request.setRequestURI("/yotereparo/./photo");
    	RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    	
        when(userService.getUserById(anyString())).thenReturn(any(User.class));
        when(userService.getUserById(anyString())).thenReturn(user);
        Assert.assertEquals(userController.getUserPhoto(anyString()).getStatusCode(), HttpStatus.OK);
    }
    
    @Test
    public void updateUserPhoto_WithValidationError_MalformedJson() {
    	ObjectMapper om = new ObjectMapper();
    	ObjectNode on = om.createObjectNode();
        Assert.assertEquals(userController.updateUserPhoto(users.get(0).getId(), on).getStatusCode(), HttpStatus.BAD_REQUEST);
    }
    
    @Test
    public void updateUserPhoto_WithValidationError_UserDoesntExist() {
    	ObjectMapper om = new ObjectMapper();
    	ObjectNode on = om.createObjectNode();
    	on.put("foto", "b64code");
    	when(userService.getUserById(anyString())).thenReturn(null);
        Assert.assertEquals(userController.updateUserPhoto(users.get(0).getId(), on).getStatusCode(), HttpStatus.NOT_FOUND);
    }
    
    @Test
    public void updateUserPhoto_Success() throws IOException {
    	byte[] buffer = ((DataBufferByte)ImageIO.read(getClass().getClassLoader().getResource("tstimage.png")).getRaster().getDataBuffer()).getData();
    	String encodedPhoto = new String(Base64.getEncoder().encode(buffer), "UTF-8");
    	ObjectMapper om = new ObjectMapper();
    	ObjectNode on = om.createObjectNode();
    	on.put("foto", encodedPhoto);
    	
    	when(userService.getUserById(anyString())).thenReturn(any(User.class));
    	doNothing().when(userService).updateUserPhotoById(anyString(), any(byte[].class));
        Assert.assertEquals(userController.updateUserPhoto(users.get(0).getId(), on).getStatusCode(), HttpStatus.OK);
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
    
    public List<UserDto> getUsersDtoList(){
    	UserDto testUser1 = new UserDto();
        testUser1.setId("testUser7");
        testUser1.setNombre("Test");
        testUser1.setApellido("User7");
        testUser1.setEmail("testuser7@yotereparo.com");
        testUser1.setCiudad("venado_tuerto");
        testUser1.setMembresia(null);
        testUser1.setEstado("TST");
        testUser1.setIntentosIngreso(0);
        testUser1.getDirecciones().add(address);
         
        UserDto testUser2 = new UserDto();
        testUser2.setId("testUser8");
        testUser2.setNombre("Test");
        testUser2.setApellido("User8");
        testUser2.setEmail("testuser2@yotereparo.com");
        testUser2.setCiudad("venado_tuerto");
        testUser2.setMembresia(null);
        testUser2.setEstado("TST");
        testUser2.setIntentosIngreso(0);
        testUser2.getDirecciones().add(address);
        
        usersDto.add(testUser1);
        usersDto.add(testUser2);
        return usersDto;
    }
}