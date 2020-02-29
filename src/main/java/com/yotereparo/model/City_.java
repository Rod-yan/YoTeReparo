package com.yotereparo.model;

import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(City.class)
public class City_ {
	public static volatile SingularAttribute<City, String> id;
	public static volatile SingularAttribute<City, String> descripcion;
	public static volatile SetAttribute<City, District> barrios;
}
