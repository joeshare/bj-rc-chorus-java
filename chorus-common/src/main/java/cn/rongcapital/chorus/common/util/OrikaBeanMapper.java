package cn.rongcapital.chorus.common.util;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import java.util.List;

/**
 * Just a simple bean mapper.
 * @author kriswang
 *
 */
public class OrikaBeanMapper {

	public static OrikaBeanMapper INSTANCE = new OrikaBeanMapper();

	private MapperFactory factory;
	
	private MapperFacade facade;

	private OrikaBeanMapper() {
		if (null == factory) {
			DefaultMapperFactory.Builder factoryBuilder = new DefaultMapperFactory.Builder();
			factory = factoryBuilder.build();
		}
		if(null == facade) {
			facade = factory.getMapperFacade();
		}
	}

	public  <S, D> List<D> mapAsList(Iterable<S> source,
			Class<D> destinationClass) {
		return facade.mapAsList(source, destinationClass);

	}
	public <S, D> D map(S source, Class<D> destinationClass) {
		return facade.map(source, destinationClass);
	}

}
