/* ---------------------------------------------------------------------- */

/*
 * Copyright (c) 2006, HEIG-Vd <philippe.waelti at heig-vd.ch>
 * All rights reserved.
 *
 * Redistribution and use in source  and  binary  forms,  with  or  without
 * modification, are permitted provided that the following  conditions  are
 * met:
 *
 *     * Redistributions of source code must  retain  the  above  copyright
 *       notice, this list of  conditions  and  the  following  disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above  copyright
 *       notice, this list of conditions and the  following  disclaimer  in
 *       the  documentation  and/or  other  materials  provided  with   the
 *       distribution.
 *
 *     * Neither the  name  of  the  HEIG-Vd   nor   the   names   of   its
 *       contributors  may  be  used  to  endorse   or   promote   products
 *       derived  from  this  software  without  specific   prior   written
 *       permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS  "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,  BUT  NOT  LIMITED
 * TO,  THE  IMPLIED  WARRANTIES  OF  MERCHANTABILITY  AND  FITNESS  FOR  A
 * PARTICULAR PURPOSE ARE DISCLAIMED.  IN  NO  EVENT  SHALL  THE  COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY  DIRECT,  INDIRECT,  INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,  DATA,  OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY  THEORY  OF
 * LIABILITY,   WHETHER   IN   CONTRACT,   STRICT   LIABILITY,   OR    TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS   SOFTWARE,   EVEN   IF   ADVISED    OF    THE    POSSIBILITY    OF
 * SUCH   DAMAGE.
 */

/* ---------------------------------------------------------------------- */

/* Simple URL -> ASCII converter
 * PWI [2006-05-19]
 * BSD License
 */

/* ---------------------------------------------------------------------- */

/* Simple Linked List */
import java.util.Hashtable;

/* HTML Parsing */
import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.*;

/* Some others... */
import java.io.*;
import java.net.*;

/* ---------------------------------------------------------------------- */

/* Parser, a simple example of what can be done... */
public class RIM_HTML_Parser {

    /* ------------------------------------------------------------------ */

    /* Output of parsing */
    private static String output;

    /* Parsed links (URL, occurences) */
    private static Hashtable<URL,Integer> links;

    /* Parser callback functions */
    private static HTMLEditorKit.ParserCallback callback =
        new HTMLEditorKit.ParserCallback () {

        public void handleStartTag(HTML.Tag t, MutableAttributeSet a,
                int pos) {

            /* Register links */
            if (t.equals(HTML.Tag.A)) {

                /* Add links to hashtable */

                /* TODO COMPLETE */

            }
        }

        public void handleSimpleTag(HTML.Tag t, MutableAttributeSet a,
                int pos) {
            /* Just add some spaces, for example after a <br> */
            output += " ";
        }

        public void handleText(char[] data, int pos) {
            /* Add the content */
            output += new String(data);
        }

        public void handleEndTag(HTML.Tag t, int pos) {
            /* Just add some spaces */
            output += " ";
        }
    };

    /* ------------------------------------------------------------------ */

    /* Read Ascii from an URL. May return `null` if any problem happens */
    public static String readURL(URL url)
    {
        /* Our (live) stream */
        InputStreamReader stream;

        /* Empty actual results */
        output = new String();
        links  = new Hashtable<URL,Integer>();

        /* Open stream */
        try {
            stream = new InputStreamReader(url.openStream());
        } catch (IOException e) {
            System.err.println("[WW] Cannot open URL");
            return null;
        }

        /* Parse */
        try {
            new ParserDelegator().parse(stream, callback, false);
        } catch (IOException e) {
            System.err.println("[EE] Parse error");
            System.exit(1);
        }

        /* Return result (without punctuation, join newlines) */
        return output.replaceAll("[\\p{Punct}|\n]", " ");
    }

    /* ------------------------------------------------------------------ */

    /* Return last parsed links */
    public static Hashtable<URL,Integer> getParsedLinks() {
        return links;
    }

    /* ------------------------------------------------------------------ */

    /* Simple test case. Usage : java RIM_HTML_Parser <url> */
    public static void main(String[] args) {

        if (args.length < 1) {
            System.err.println("[EE] Argument(s) missing");
            System.exit(1);
        }

        /* Parse URL and print content */
        try {
            System.out.println(RIM_HTML_Parser.readURL(new URL(args[0])));
        } catch (MalformedURLException e) {
            System.err.println("[EE] Malformed URL");
            System.exit(1);
        }

    }

    /* ------------------------------------------------------------------ */

}

/* ---------------------------------------------------------------------- */
