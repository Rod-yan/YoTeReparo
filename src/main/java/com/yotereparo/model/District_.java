package com.yotereparo.model;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(District.class)
public class District_ {
	public static volatile SingularAttribute<District, Integer> id;
	public static volatile SingularAttribute<District, String> descripcion;
	public static volatile SingularAttribute<District, Integer> codigoPostal;
}
