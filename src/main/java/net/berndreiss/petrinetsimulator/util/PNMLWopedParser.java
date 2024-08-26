package net.berndreiss.petrinetsimulator.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * Diese Klasse implementiert die Grundlage für einen einfachen PNML Parser, der
 * mit WoPeD PNML 1.3.2 kompatibel ist.
 */
public class PNMLWopedParser {

	/**
	 * Mit dieser Main Methode kann der Parser zum Testen aufgerufen werden. Als
	 * erster und einziger Paramter muss dazu der Pfad zur PNML Datei angegeben
	 * werden.
	 * 
	 * @param args
	 *            Die Konsolen Parameter, mit denen das Programm aufgerufen
	 *            wird.
	 */
	public static void main(final String[] args) {
		if (args.length > 0) {
			File pnmlDatei = new File(args[0]);
			if (pnmlDatei.exists()) {
				PNMLWopedParser pnmlParser = new PNMLWopedParser(pnmlDatei);
				pnmlParser.initParser();
				pnmlParser.parse();
			} else {
				System.err.println("Die Datei " + pnmlDatei.getAbsolutePath()
						+ " wurde nicht gefunden!");
			}
		} else {
			System.out.println("Bitte eine Datei als Parameter angeben!");
		}
	}

	/**
	 * Dies ist eine Referenz zum Java Datei Objekt.
	 */
	private File pnmlDatei;

	/**
	 * Dies ist eine Referenz zum XML Parser. Diese Referenz wird durch die
	 * Methode parse() initialisiert.
	 */
	private XMLEventReader xmlParser = null;

	/**
	 * Diese Variable dient als Zwischenspeicher für die ID des zuletzt
	 * gefundenen Elements.
	 */
	private String lastId = null;

	/**
	 * Dieses Flag zeigt an, ob der Parser gerade innerhalb eines InitialMarking
	 * Elements liest.
	 */
	private boolean isInitialMarking = false;

	/**
	 * Dieses Flag zeigt an, ob der Parser gerade innerhalb eines Name Elements
	 * liest.
	 */
	private boolean isName = false;

	/**
	 * Dieses Flag zeigt an, ob der Parser gerade innerhalb eines Text Elements
	 * liest.
	 */
	private boolean isText = false;

	/**
	 * Dieser Konstruktor erstellt einen neuen Parser für PNML Dateien, dem die
	 * PNML Datei als Java {@link File} übergeben wird.
	 * 
	 * @param pnml
	 *            Java {@link File} Objekt der PNML Datei
	 */
	public PNMLWopedParser(final File pnml) {
		super();

		this.pnmlDatei = pnml;
	}

	/**
	 * Diese Methode öffnet die PNML Datei als Eingabestrom und initialisiert
	 * den XML Parser.
	 */
	public final void initParser() {
		try {
			InputStream dateiEingabeStrom = new FileInputStream(pnmlDatei);
			XMLInputFactory factory = XMLInputFactory.newInstance();
			try {
				xmlParser = factory.createXMLEventReader(dateiEingabeStrom);

			} catch (XMLStreamException e) {
				System.err
						.println("XML Verarbeitungsfehler: " + e.getMessage());
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			System.err.println("Die Datei wurde nicht gefunden! "
					+ e.getMessage());
		}
	}

	/**
	 * Diese Methode liest die XML Datei und delegiert die gefundenen XML
	 * Elemente an die entsprechenden Methoden.
	 */
	public final void parse() {
		while (xmlParser.hasNext()) {
			try {
				XMLEvent event = xmlParser.nextEvent();
				switch (event.getEventType()) {
				case XMLStreamConstants.START_ELEMENT:
					handleStartEvent(event);
					break;
				case XMLStreamConstants.END_ELEMENT:
					String name = event.asEndElement().getName().toString()
							.toLowerCase();
					if (name.equals("initialmarking")) {
						isInitialMarking = false;
						// ----------------
					} else if (name.equals("name")) {
						isName = false;

					} else if (name.equals("text")) {
						isText = false;
						// ----------------
					}
					break;
				case XMLStreamConstants.CHARACTERS:
					if (isText && lastId != null) {
						Characters ch = event.asCharacters();
						if (!ch.isWhiteSpace()) {
							handleText(ch.getData());
						}
					}
					break;
				case XMLStreamConstants.END_DOCUMENT:
					// schließe den Parser
					xmlParser.close();
					break;
				default:
				}
			} catch (XMLStreamException e) {
				System.err.println("Fehler beim Parsen des PNML Dokuments. "
						+ e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 * Diese Methode behandelt den Start neuer XML Elemente, in dem der Name des
	 * Elements überprüft wird und dann die Behandlung an spezielle Methoden
	 * delegiert wird.
	 * 
	 * @param event
	 *            {@link XMLEvent}
	 */
	private void handleStartEvent(final XMLEvent event) {
		StartElement element = event.asStartElement();
		if (element.getName().toString().toLowerCase().equals("transition")) {
			handleTransition(element);
		} else if (element.getName().toString().toLowerCase().equals("place")) {
			handlePlace(element);
		} else if (element.getName().toString().toLowerCase().equals("arc")) {
			handleArc(element);
		} else if (element.getName().toString().toLowerCase().equals("name")) {
			isName = true;
		} else if (element.getName().toString().toLowerCase()
				.equals("position")) {
			handlePosition(element);
		} else if (element.getName().toString().toLowerCase()
				.equals("initialmarking")) {
			isInitialMarking = true;
		} else if (element.getName().toString().toLowerCase().equals("text")) {
			isText = true;
		}
	}

	/**
	 * Diese Methode wird aufgerufen, wenn eine Zeichenkette innerhalb eines
	 * Text Elements gelesen wird.
	 * 
	 * @param text
	 *            Der gelesene Text als String
	 */
	private void handleText(final String text) {
		if (isName) {
			setName(lastId, text);
		} else if (isInitialMarking) {
			setTokens(lastId, text);
		}
	}

	/**
	 * Diese Methode wird aufgerufen, wenn ein Positionselement gelesen wird.
	 * 
	 * @param element
	 *            das Positionselement
	 */
	private void handlePosition(final StartElement element) {
		String x = null;
		String y = null;
		Iterator<?> attributes = element.getAttributes();
		while (attributes.hasNext()) {
			Attribute attr = (Attribute) attributes.next();
			if (attr.getName().toString().toLowerCase().equals("x")) {
				x = attr.getValue();
			} else if (attr.getName().toString().toLowerCase().equals("y")) {
				y = attr.getValue();
			}
		}
		if (x != null && y != null && lastId != null) {
			setPosition(lastId, x, y);
		}
		/*
		 * Workaround für WoPeD PNML-Dateien: Das WoPeD spezifische Element
		 * <toolspecific> enthält auch ein Element <position>, welches der
		 * Parser ignorieren soll.
		 */
		else if (x != null && y != null && lastId == null) {
			// keine Meldung ausgeben
			//System.out.println("Position ohne ID wird ignoriert.");
		} else {
			System.err.println("Unvollständige Position wurde verworfen!");
		}
	}

	/**
	 * Diese Methode wird aufgerufen, wenn ein Transitionselement gelesen wird.
	 * 
	 * @param element
	 *            das Transitionselement
	 */
	private void handleTransition(final StartElement element) {
		String transitionId = null;
		Iterator<?> attributes = element.getAttributes();
		while (attributes.hasNext()) {
			Attribute attr = (Attribute) attributes.next();
			if (attr.getName().toString().toLowerCase().equals("id")) {
				transitionId = attr.getValue();
				break;
			}
		}
		if (transitionId != null) {
			newTransition(transitionId);
			lastId = transitionId;
		} else {
			System.err.println("Transition ohne id wurde verworfen!");
			lastId = null;
		}
	}

	/**
	 * Diese Methode wird aufgerufen, wenn ein Stellenelement gelesen wird.
	 * 
	 * @param element
	 *            das Stellenelement
	 */
	private void handlePlace(final StartElement element) {
		String placeId = null;
		Iterator<?> attributes = element.getAttributes();
		while (attributes.hasNext()) {
			Attribute attr = (Attribute) attributes.next();
			if (attr.getName().toString().toLowerCase().equals("id")) {
				placeId = attr.getValue();
				break;
			}
		}
		if (placeId != null) {
			newPlace(placeId);
			lastId = placeId;
		} else {
			System.err.println("Stelle ohne id wurde verworfen!");
			lastId = null;
		}
	}

	/**
	 * Diese Methode wird aufgerufen, wenn ein Kantenelement gelesen wird.
	 * 
	 * @param element
	 *            das Kantenelement
	 */
	private void handleArc(final StartElement element) {
		String arcId = null;
		String source = null;
		String target = null;
		Iterator<?> attributes = element.getAttributes();
		while (attributes.hasNext()) {
			Attribute attr = (Attribute) attributes.next();
			if (attr.getName().toString().toLowerCase().equals("id")) {
				arcId = attr.getValue();
			} else if (attr.getName().toString().toLowerCase().equals("source")) {
				source = attr.getValue();
			} else if (attr.getName().toString().toLowerCase().equals("target")) {
				target = attr.getValue();
			}
		}
		if (arcId != null && source != null && target != null) {
			newArc(arcId, source, target);
		} else {
			System.err.println("Unvollständige Kante wurde verworfen!");
		}
		// Die id von Kanten wird nicht gebraucht.
		lastId = null;
	}

	/**
	 * Diese Methode kann überschrieben werden, um geladene Transitionen zu
	 * erstellen.
	 * 
	 * @param id
	 *            Identifikationstext der Transition
	 */
	public void newTransition(final String id) {
		System.out.println("Transition mit id " + id + " wurde gefunden.");
	}

	/**
	 * Diese Methode kann überschrieben werden, um geladene Stellen zu
	 * erstellen.
	 * 
	 * @param id
	 *            Identifikationstext der Stelle
	 */
	public void newPlace(final String id) {
		System.out.println("Stelle mit id " + id + " wurde gefunden.");
	}

	/**
	 * Diese Methode kann überschrieben werden, um geladene Kanten zu erstellen.
	 * 
	 * @param id
	 *            Identifikationstext der Kante
	 * @param source
	 *            Identifikationstext des Startelements der Kante
	 * @param target
	 *            Identifikationstext des Endelements der Kante
	 */
	public void newArc(final String id, final String source, final String target) {
		System.out.println("Kante mit id " + id + " von " + source + " nach "
				+ target + " wurde gefunden.");
	}

	/**
	 * Diese Methode kann überschrieben werden, um die Positionen der geladenen
	 * Elemente zu aktualisieren.
	 * 
	 * @param id
	 *            Identifikationstext des Elements
	 * @param x
	 *            x Position des Elements
	 * @param y
	 *            y Position des Elements
	 */
	public void setPosition(final String id, final String x, final String y) {
		System.out.println("Setze die Position des Elements " + id + " auf ("
				+ x + ", " + y + ")");
	}

	/**
	 * Diese Methode kann überschrieben werden, um den Beschriftungstext der
	 * geladenen Elemente zu aktualisieren.
	 * 
	 * @param id
	 *            Identifikationstext des Elements
	 * @param name
	 *            Beschriftungstext des Elements
	 */
	public void setName(final String id, final String name) {
		System.out.println("Setze den Namen des Elements " + id + " auf "
				+ name);
	}

	/**
	 * Diese Methode kann überschrieben werden, um die Markenanzahl des
	 * geladenen Elements zu aktualisieren.
	 * 
	 * @param id
	 *            Identifikationstext des Elements
	 * @param tokens
	 *            Markenanzahl des Elements
	 */
	public void setTokens(final String id, final String tokens) {
		System.out.println("Setze die Markenanzahl des Elements " + id
				+ " auf " + tokens);
	}
}
