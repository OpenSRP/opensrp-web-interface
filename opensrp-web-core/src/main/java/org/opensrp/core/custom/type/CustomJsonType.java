package org.opensrp.core.custom.type;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

public class CustomJsonType implements UserType {
	
	@Override
	public int[] sqlTypes() {
		return new int[] { Types.JAVA_OBJECT };
	}
	
	@Override
	public Class<CustomJson> returnedClass() {
		return CustomJson.class;
	}
	
	@Override
	public Object deepCopy(final Object value) throws HibernateException {
		try {
			// use serialization to create a deep copy
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(value);
			oos.flush();
			oos.close();
			bos.close();
			
			ByteArrayInputStream bais = new ByteArrayInputStream(bos.toByteArray());
			return new ObjectInputStream(bais).readObject();
		}
		catch (ClassNotFoundException | IOException ex) {
			throw new HibernateException(ex);
		}
	}
	
	@Override
	public boolean isMutable() {
		return true;
	}
	
	@Override
	public Serializable disassemble(final Object value) throws HibernateException {
		return (Serializable) this.deepCopy(value);
	}
	
	@Override
	public Object assemble(final Serializable cached, final Object owner) throws HibernateException {
		return this.deepCopy(cached);
	}
	
	@Override
	public Object replace(final Object original, final Object target, final Object owner) throws HibernateException {
		return this.deepCopy(original);
	}
	
	@Override
	public boolean equals(final Object obj1, final Object obj2) throws HibernateException {
		if (obj1 == null) {
			return obj2 == null;
		}
		return obj1.equals(obj2);
	}
	
	@Override
	public int hashCode(final Object obj) throws HibernateException {
		return obj.hashCode();
	}
	
	@Override
	public Object nullSafeGet(ResultSet arg0, String[] arg1, SharedSessionContractImplementor arg2, Object arg3)
	    throws HibernateException, SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void nullSafeSet(PreparedStatement arg0, Object arg1, int arg2, SharedSessionContractImplementor arg3)
	    throws HibernateException, SQLException {
		// TODO Auto-generated method stub
		
	}
	
}
