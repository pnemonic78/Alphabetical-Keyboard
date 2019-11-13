package com.github.inputmethod.alphabetical.sorter;

import com.sun.org.apache.xml.internal.serializer.OutputPropertiesFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
        String path = "keys.xml";
        boolean reverse = false;
        if (args.length > 0) {
            String args0 = args[0];
            if (args0.equals("-reverse") || args0.equals("-rtl")) {
                reverse = true;
            } else {
                path = args0;
            }
        }
        new SortKeys().sort(path, reverse);
    }

    public void sort(String path, boolean reverse) throws Exception {
        sort(new File(path), reverse);
    }

    public void sort(File file, boolean reverse) throws Exception {
        Document document = parse(file);
        sort(document, reverse);
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

    private void sort(Document document, boolean reverse) {
        Node root = document.getFirstChild();
        Node node;
        short nodeType;
        String nodeName;
        Node comment1 = null;
        Node comment2 = null;
        Node key;
        KeyPair pair;
        final List<KeyPair> pairs = new ArrayList<>();

        while (root.hasChildNodes()) {
            node = root.getFirstChild();
            root.removeChild(node);
            nodeType = node.getNodeType();
            if (nodeType == Node.COMMENT_NODE) {
                if (comment1 == null) {
                    comment1 = node;
                } else if (comment2 == null) {
                    comment2 = node;
                }
            } else if (nodeType == Node.ELEMENT_NODE) {
                nodeName = node.getNodeName();
                if (nodeName.equals(ELEMENT_KEY)) {
                    key = node;
                    pair = new KeyPair(comment1, comment2, key);
                    pairs.add(pair);
                    comment1 = null;
                    comment2 = null;
                }
            }
        }

        Comparator<KeyPair> comparator = new KeyPairComparator();
        Collections.sort(pairs, comparator);
        if (reverse) {
            Collections.reverse(pairs);
        }
        for (KeyPair pair2 : pairs) {
            if (pair2.comment1 != null) {
                root.appendChild(pair2.comment1);
            }
            if (pair2.comment2 != null) {
                root.appendChild(pair2.comment2);
            }
            root.appendChild(pair2.key);
            System.out.println(pair2.comparable);
        }
    }

    private static class KeyPair implements Comparable<KeyPair> {

        private static final String ATTRIBUTE_TO_SORT = "latin:keySpec";

        final Node comment1;
        final Node comment2;
        final Node key;
        final String comparable;

        private KeyPair(Node comment, Node key) {
            this(comment, null, key);
        }

        private KeyPair(Node comment1, Node comment2, Node key) {
            this.comment1 = comment1;
            this.comment2 = comment2;
            this.key = key;
            this.comparable = key.getAttributes().getNamedItem(ATTRIBUTE_TO_SORT).getNodeValue();
        }

        @Override
        public int compareTo(KeyPair that) {
            return comparable.compareTo(that.comparable);
        }
    }

    private static class KeyPairComparator implements Comparator<KeyPair> {

        @Override
        public int compare(KeyPair k1, KeyPair k2) {
            return k1.compareTo(k2);
        }
    }
}
