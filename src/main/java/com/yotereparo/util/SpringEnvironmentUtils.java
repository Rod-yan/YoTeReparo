package com.yotereparo.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;

public class SpringEnvironmentUtils {
	
	public static Map<String,Object> getPropertiesStartingWith( ConfigurableEnvironment aEnv,
			String aKeyPrefix )
	{
		Map<String,Object> result = new HashMap<>();

		Map<String,Object> map = getAllProperties( aEnv );

		for (Entry<String, Object> entry : map.entrySet())
		{
			String key = entry.getKey();

			if ( key.startsWith( aKeyPrefix ) )
			{
				result.put( key, entry.getValue() );
			}
		}

		return result;
	}

	public static Map<String,Object> getAllProperties( ConfigurableEnvironment aEnv )
	{
		Map<String,Object> result = new HashMap<>();
		aEnv.getPropertySources().forEach( ps -> addAll( result, getAllProperties( ps ) ) );
		return result;
	}

	public static Map<String,Object> getAllProperties( PropertySource<?> aPropSource )
	{
		Map<String,Object> result = new HashMap<>();

		if ( aPropSource instanceof CompositePropertySource)
		{
			CompositePropertySource cps = (CompositePropertySource) aPropSource;
			cps.getPropertySources().forEach( ps -> addAll( result, getAllProperties( ps ) ) );
			return result;
		}

		if ( aPropSource instanceof EnumerablePropertySource<?> )
		{
			EnumerablePropertySource<?> ps = (EnumerablePropertySource<?>) aPropSource;
			Arrays.asList( ps.getPropertyNames() ).forEach( key -> result.put( key, ps.getProperty( key ) ) );
			return result;
		}

		return result;

	}

	private static void addAll( Map<String, Object> aBase, Map<String, Object> aToBeAdded )
	{
		for (Entry<String, Object> entry : aToBeAdded.entrySet())
		{
			if ( aBase.containsKey( entry.getKey() ) )
			{
				continue;
			}

			aBase.put( entry.getKey(), entry.getValue() );
		}
	}
}
