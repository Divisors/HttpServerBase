package com.divisors.projectcuttlefish.httpserver.api.http;

public enum StandardMimeTypes implements MimeType {
	APPLICATION_X_BYTECODE_PYTHON("applicaiton/x-bytecode.python"),
	APPLICATION_ACAD("application/acad"),
	APPLICATION_ARJ("application/arj"),
	APPLICATION_BASE64("application/base64"),
	APPLICATION_BINHEX("application/binhex"),
	APPLICATION_BINHEX4("application/binhex4"),
	APPLICATION_BOOK("application/book"),
	APPLICATION_CDF("application/cdf"),
	APPLICATION_CLARISCAD("application/clariscad"),
	APPLICATION_COMMONGROUND("application/commonground"),
	APPLICATION_DRAFTING("application/drafting"),
	APPLICATION_DSPTYPE("application/dsptype"),
	APPLICATION_DXF("application/dxf"),
	APPLICAITON_X_BYTECODE_PYTHON("application/x-bytecode.python"),
	APPLICATION_EXCEL,
	APPLICATION_JAVA,
	APPLICATION_JAVA_BYTE_CODE,
	APPLICATION_LHA,
	APPLICATION_LZX,
	APPLICATION_MIME,
	APPLICATION_MSPOWERPOINT,
	APPLICATION_MSWORD,
	APPLICATION_MSWRITE,
	APPLICATION_OCTET_STREAM("application/octet-stream"),
	APPLICATION_PDF,
	APPLICATION_PKCS_12,
	APPLICATION_PKCS_CRL,
	APPLICATION_PKCS10,
	APPLICATION_PKCS7_MIME,
	APPLICATION_PKCS7_SIGNATURE,
	APPLICATION_PKIX_CERT,
	APPLICATION_PKIX_CRL,
	APPLICATION_PLAIN,
	APPLICATION_RTF,
	APPLICATION_SMIL,
	APPLICATION_STREAMINGMEDIA,
	APPLICATION_X_BINARY("application/x-binary"),
	APPLICATION_X_BINHEX40("application/x-binhex40"),
	APPLICATION_X_BSH("application/x-bsh"),
	APPLICATION_X_BZIP("application/x-bzip"),
	APPLICATION_X_BZIP2("application/x-bzip2"),
	APPLICATION_X_CHAT,
	APPLICATION_X_COMPRESS,
	APPLICATION_X_COMPRESSED,
	APPLICATION_X_EXCEL,
	APPLICATION_X_HTTPD_IMAP,
	APPLICATION_X_INTERNETT_SIGNUP,
	APPLICATION_X_INVENTOR,
	APPLICATION_X_JAVA_CLASS,
	APPLICATION_X_JAVA_COMMERCE,
	APPLICATION_X_JAVASCRIPT,
	APPLICATION_X_LATEX,
	APPLICATION_X_MAGIC_CAP_PACKAGE_1_0,
	APPLICATION_X_MEME,
	APPLICATION_X_MIDI,
	APPLICATION_X_MPLAYER2,
	APPLICATION_X_MSEXCEL,
	APPLICATION_X_MSPOWERPOINT,
	APPLICATION_X_OMCDATAMAKER,
	APPLICATION_X_OMCREGERATOR,
	APPLICATION_X_PAGEMAKER,
	APPLICATION_X_PCL,
	APPLICATION_X_PIXCLSCRIPT,
	APPLICATION_X_PKCS10,
	APPLICATION_X_PKCS12,
	APPLICATION_X_PKCS7_CERTIFICATES,
	APPLICATION_X_PKCS7_CERTREQRESP,
	APPLICATION_X_PKCS7_MIME,
	APPLICATION_X_PKCS7_SIGNATURE,
	APPLICATION_X_RTF,
	APPLICATION_X_SHOCKWAVE_FLASH,
	APPLICATION_X_TAR,
	APPLICATION_X_TCL,
	APPLICATION_X_TEX,
	APPLICATION_X_X509_CA_CERT,
	APPLICATION_X_X509_USER_CERT,
	APPLICATION_X_ZIP_COMPRESSED,
	APPLICATION_XML,
	APPLICATION_ZIP,
	AUDIO_AIFF,
	AUDIO_BASIC,
	AUDIO_MIDI,
	AUDIO_MPEG,
	AUDIO_MPEG3,
	AUDIO_WAV,
	IMAGE_BMP,
	IMAGE_GIF,
	IMAGE_JPEG,
	IMAGE_PNG,
	IMAGE_TIFF,
	IMAGE_X_CMU_RASTER,
	IMAGE_X_DWG,
	IMAGE_X_ICON,
	IMAGE_X_JG,
	IMAGE_X_JPS,
	IMAGE_X_NIFF,
	IMAGE_X_PCX,
	IMAGE_X_PICT,
	IMAGE_X_PORTABLE_ANYMAP,
	IMAGE_X_PORTABLE_BITMAP,
	IMAGE_X_PORTABLE_GRAYMAP,
	IMAGE_X_PORTABLE_PIXMAP,
	IMAGE_X_RGB,
	IMAGE_X_TIFF,
	IMAGE_X_WINDOWS_BMP,
	MESSAGE_RFC822,
	MULTIPART_X_GZIP,
	MULTIPART_X_USTAR,
	MULTIPART_X_ZIP,
	TEXT_ASP,
	TEXT_CSS,
	TEXT_HTML,
	TEXT_MCF,
	TEXT_PASCAL,
	TEXT_PLAIN,
	TEXT_RICHTEXT,
	TEXT_SCRIPLET,
	TEXT_SGML,
	TEXT_TAB_SEPARATED_VALUES,
	TEXT_URI_LIST,
	TEXT_WEBVIEWHTML,
	TEXT_X_ASM,
	TEXT_X_AUDIOSOFT_INTRA,
	TEXT_X_C,
	TEXT_X_COMPONENT,
	TEXT_X_FORTRAN,
	TEXT_X_H,
	TEXT_X_JAVA_SOURCE,
	TEXT_X_LA_ASF,
	TEXT_X_M,
	TEXT_X_PASCAL,
	TEXT_X_SCRIPT,
	TEXT_X_SCRIPT_CSH,
	TEXT_X_SCRIPT_ELISP,
	TEXT_X_SCRIPT_GUILE,
	TEXT_X_SCRIPT_KSH,
	TEXT_X_SCRIPT_LISP,
	TEXT_X_SCRIPT_PERL,
	TEXT_X_SCRIPT_PERL_MODULE,
	TEXT_X_SCRIPT_PHYTON,
	TEXT_X_SCRIPT_REXX,
	TEXT_X_SCRIPT_SCHEME,
	TEXT_X_SCRIPT_SH,
	TEXT_X_SCRIPT_TCL,
	TEXT_X_SCRIPT_TCSH,
	TEXT_X_SCRIPT_ZSH,
	TEXT_X_SERVER_PARSED_HTML,
	TEXT_X_SETEXT,
	TEXT_X_SGML,
	TEXT_X_SPEECH,
	TEXT_X_UIL,
	TEXT_X_UUENCODE,
	TEXT_X_VCALENDAR,
	TEXT_XML,
	VIDEO_ANIMAFLEX,
	VIDEO_AVI,
	VIDEO_AVS_VIDEO,
	VIDEO_MPEG,
	VIDEO_MSVIDEO,
	VIDEO_QUICKTIME,
	VIDEO_VND_RN_REALVIDEO,
	VIDEO_VND_VIVO,
	VIDEO_VOSAIC,
	VIDEO_X_AMT_DEMORUN,
	VIDEO_X_AMT_SHOWRUN,
	VIDEO_X_ATOMIC3D_FEATURE,
	VIDEO_X_DL,
	VIDEO_X_DV,
	VIDEO_X_FLI,
	VIDEO_X_GL,
	VIDEO_X_ISVIDEO,
	VIDEO_X_MOTION_JPEG,
	VIDEO_X_MPEG,
	VIDEO_X_MPEQ2A,
	VIDEO_X_MS_ASF,
	VIDEO_X_MS_ASF_PLUGIN,
	VIDEO_X_MSVIDEO,
	VIDEO_X_QTC,
	VIDEO_X_SCM,
	VIDEO_X_SGI_MOVIE,
	;
	private final String type;
	private final String subtype;
	StandardMimeTypes() {
		String name = this.name();
		type = name.substring(0, name.indexOf('_')).toLowerCase();
		subtype = name.substring(name.indexOf('_') + 1).toLowerCase().replace('_', '-');
	}
	StandardMimeTypes(String type) {
		this(type.substring(0, type.indexOf('/')), type.substring(type.indexOf('/') + 1));
	}
	StandardMimeTypes(String type, String subtype) {
		this.type = type;
		this.subtype = subtype;
	}
	@Override
	public String getType() {
		return this.type;
	}

	@Override
	public String getSubtype() {
		return subtype;
	}
	@Override
	public String toString() {
		return getType() + "/" + getSubtype();
	}
}
