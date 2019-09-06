package org.openmrs.module.callflows.api.util;

import org.apache.commons.io.IOUtils;
import org.openmrs.module.callflows.api.exception.CallFlowRuntimeException;

import java.io.IOException;
import java.io.InputStream;

public final class ResourceUtil {

	public static boolean resourceFileExists(String filename) {
		return ResourceUtil.class.getClassLoader().getResource(filename) != null;
	}

	public static String readResourceFile(String filename) throws CallFlowRuntimeException {
		try (InputStream in = ResourceUtil.class.getClassLoader().getResourceAsStream(filename)) {
			if (in == null) {
				throw new CallFlowRuntimeException("Resource '" + filename + "' doesn't exist");
			}
			return IOUtils.toString(in);
		} catch (IOException e) {
			throw new CallFlowRuntimeException(e);
		}
	}

	private ResourceUtil() { }

}
