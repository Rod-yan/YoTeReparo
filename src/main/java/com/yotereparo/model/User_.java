package com.yotereparo.model;

import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(User.class)
public class User_ {
	public static volatile SetAttribute<User, District> barrios;
}
