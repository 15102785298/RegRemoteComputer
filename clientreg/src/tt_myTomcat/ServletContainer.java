package tt_myTomcat;

import java.util.HashMap;
import java.util.Map;

public class ServletContainer {
	private static Map<String, Object> servletMaps = new HashMap<String, Object>();
	private static Map<String, Object> servletMappingMaps = new HashMap<String, Object>();
	private static Map<String, HttpServlet> servletContainer = new HashMap<String, HttpServlet>();

	static {
		try {
			Map<Integer, Map<String, Object>> maps = XMLUtil.parseWebXML();
			if (null != maps && 2 == maps.size()) {
				servletMaps = maps.get(0);
				servletMappingMaps = maps.get(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static HttpServlet getHttpServlet(String path) {

		if (null == path || "".equals(path.trim()) || "/".equals(path)) {
			path = "/index";
		}

		if (servletContainer.containsKey(path)) {
			return servletContainer.get(path);
		}

		if (!servletMappingMaps.containsKey(path)) {
			return null;
		}
		ServletMapping servletMapping = (ServletMapping) servletMappingMaps.get(path);
		String name = servletMapping.getName();

		if (!servletMaps.containsKey(name)) {
			return null;
		}
		Servlet servlet = (Servlet) servletMaps.get(name);
		String clazz = servlet.getClazz();

		if (null == clazz || "".equals(clazz.trim())) {
			return null;
		}

		HttpServlet httpServlet = null;
		try {
			httpServlet = (HttpServlet) Class.forName(clazz).newInstance();
			servletContainer.put(path, httpServlet);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return httpServlet;
	}
}
