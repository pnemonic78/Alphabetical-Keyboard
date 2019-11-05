package com.github.inputmethod.alphabetical.sorter;

import com.sun.org.apache.xml.internal.serializer.OutputPropertiesFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Sort an XML with keyboard keys.
 */
public class SortKeys {

    private static final String ELEMENT_KEY = "Key";

    public static void main(String[] args) throws Exception {
        String path;
        if (args.length > 0) {
            path = args[0];
        } else {
            path = "keys.xml";
        }
        new SortKeys().sort(path);
    }

    public void sort(String path) throws Exception {
        sort(new File(path));
    }

    public void sort(File file) throws Exception {
        Document document = parse(file);
        sort(document);
        write(document, file);
    }

    private Document parse(File file) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setExpandEntityReferences(false);
        documentBuilderFactory.setIgnoringComments(false);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        return documentBuilder.parse(file);
    }

    private void write(Document document, File file) throws TransformerException {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputPropertiesFactory.S_KEY_INDENT_AMOUNT, "4");
        DOMSource source = new DOMSource(document);
        File fileOut = file;
        StreamResult result = new StreamResult(fileOut);
        transformer.transform(source, result);
    }

    private void sort(Document document) {
        Node root = document.getFirstChild();
        Node node;
        short nodeType;
        String nodeName;
        Node comment = null;
        Node key;
        KeyPair pair;
        final SortedSet<KeyPair> pairs = new TreeSet<>();

        while (root.hasChildNodes()) {
            node = root.getFirstChild();
            root.removeChild(node);
            nodeType = node.getNodeType();
            if (nodeType == Node.COMMENT_NODE) {
                comment = node;
            } else if (nodeType == Node.ELEMENT_NODE) {
                nodeName = node.getNodeName();
                if (nodeName.equals(ELEMENT_KEY)) {
                    key = node;
                    pair = new KeyPair(comment, key);
                    pairs.add(pair);
                    comment = null;
                }
            }
        }

        for (KeyPair pair2 : pairs) {
            root.appendChild(pair2.comment);
            root.appendChild(pair2.key);
            System.out.println(pair2.comparable);
        }
    }

    private static class KeyPair implements Comparable<KeyPair> {

        private static final String ATTRIBUTE_TO_SORT = "latin:keySpec";

        final Node comment;
        final Node key;
        final String comparable;

        private KeyPair(Node comment, Node key) {
            this.comment = comment;
            this.key = key;
            this.comparable = key.getAttributes().getNamedItem(ATTRIBUTE_TO_SORT).getNodeValue();
        }

        @Override
        public int compareTo(KeyPair that) {
            return comparable.compareTo(that.comparable);
        }
    }
}
