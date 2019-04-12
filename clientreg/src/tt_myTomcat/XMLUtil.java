package tt_myTomcat;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class XMLUtil {

    public static Map<Integer, Map<String, Object>> parseWebXML() throws Exception {
        Map<Integer, Map<String, Object>> result = new HashMap<Integer, Map<String,Object>>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        InputStream in = XMLUtil.class.getClassLoader().getResourceAsStream("web.xml");
        Document document = db.parse(in);
        Element root = document.getDocumentElement();
        NodeList xmlNodes = root.getChildNodes();
        for (int i = 0; i < xmlNodes.getLength(); i++) {
            Node config = xmlNodes.item(i);
            if (null != config && config.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName1 = config.getNodeName();
                if ("servlet".equals(nodeName1)) {
                    Map<String, Object> servletMaps = null;
                    if (result.containsKey(0)) {
                        servletMaps = result.get(0);
                    } else {
                        servletMaps = new HashMap<String, Object>();
                    }

                    NodeList childNodes = config.getChildNodes();
                    Servlet servlet = new Servlet();
                    for (int j = 0; j < childNodes.getLength(); j++) {
                        Node node = childNodes.item(j);
                        if (null != node && node.getNodeType() == Node.ELEMENT_NODE) {
                            String nodeName2 = node.getNodeName();
                            String textContent = node.getTextContent();
                            if ("servlet-name".equals(nodeName2)) {
                                servlet.setName(textContent);
                            } else if ("servlet-class".equals(nodeName2)) {
                                servlet.setClazz(textContent);
                            }
                        }
                    }
                    servletMaps.put(servlet.getName(), servlet);

                    result.put(0, servletMaps);
                } else if ("servlet-mapping".equals(nodeName1)) {
                    Map<String, Object> servletMappingMaps = null;
                    if (result.containsKey(1)) {
                        servletMappingMaps = result.get(1);
                    } else {
                        servletMappingMaps = new HashMap<String, Object>();
                    }

                    NodeList childNodes = config.getChildNodes();
                    ServletMapping servletMapping = new ServletMapping();
                    for (int j = 0; j < childNodes.getLength(); j++) {
                        Node node = childNodes.item(j);
                        if (null != node && node.getNodeType() == Node.ELEMENT_NODE) {
                            String nodeName2 = node.getNodeName();
                            String textContent = node.getTextContent();
                            if ("servlet-name".equals(nodeName2)) {
                                servletMapping.setName(textContent);
                            } else if ("url-pattern".equals(nodeName2)) {
                                servletMapping.setUrl(textContent);
                            }
                        }
                    }
                    servletMappingMaps.put(servletMapping.getUrl(), servletMapping);

                    result.put(1, servletMappingMaps);
                }
            }
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(parseWebXML());
    }
}