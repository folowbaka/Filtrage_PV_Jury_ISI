package data;
/**
 * Note represente la note possible pour une UV
 */
public enum Note{
	A("A"),
	B("B"),
	C("C"),
	D("D"),
	E("E"),
	F("F"),
	FX("FX"),
	EQU("EQU"),
	ABS("ABS"),
	NULL("NULL");

	/**Represente la note en chaine de caratères*/
	private String note;

	/**
	 * Constructeur d'une note
	 * @param note la note 
	 */
	Note(String note){
		this.note = note;
	}

	/** 
	 * Getter pour recuperer la note en chaine de caractères
	 * @return la note
	 */
	public String getNote() {
		return note;
	}

	/**
	 * methode qui converti la chaine de la note en enumeration
	 * @param note la note en chaine
	 * @return la Note en enum
	 */
	public static Note getNote(String note) {
		if(note.equals("A"))
				return A;
		if(note.equals("B"))
				return B;
		if(note.equals("C"))
				return C;
		if(note.equals("D"))
				return D;
		if(note.equals("E"))
				return E;
		if(note.equals("F"))
				return F;
		if(note.equals("FX"))
				return FX;
		if(note.equals("EQU"))
				return EQU;
		if(note.equals("ABS"))
				return ABS;
		if(note.equals("NULL"))
				return NULL;
		return null;
	}
}
