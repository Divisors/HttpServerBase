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
	APPLICATION_ENVOY,
	APPLICATION_EXCEL,
	APPLICATION_FRACTALS,
	APPLICATION_FREELOADER,
	APPLICATION_FUTURESPLASH,
	APPLICATION_GNUTAR,
	APPLICATION_GROUPWISE,
	APPLICATION_HLP,
	APPLICATION_HTA,
	APPLICATION_I_DEAS("application/i-deas"),
	APPLICATION_IGES,
	APPLICATION_INF,
	APPLICATION_JAVA,
	APPLICATION_JAVA_BYTE_CODE,
	APPLICATION_LHA,
	APPLICATION_LZX,
	APPLICATION_MAC_BINARY,
	APPLICATION_MAC_BINHEX,
	APPLICATION_MAC_BINHEX40,
	APPLICATION_MAC_COMPACTPRO,
	APPLICATION_MACBINARY,
	APPLICATION_MARC,
	APPLICATION_MBEDLET,
	APPLICATION_MCAD,
	APPLICATION_MIME,
	APPLICATION_MSPOWERPOINT,
	APPLICATION_MSWORD,
	APPLICATION_MSWRITE,
	APPLICATION_NETMC,
	APPLICATION_OCTET_STREAM("application/octet-stream"),
	APPLICATION_ODA,
	APPLICATION_PDF,
	APPLICATION_PKCS_12,
	APPLICATION_PKCS_CRL,
	APPLICATION_PKCS10,
	APPLICATION_PKCS7_MIME,
	APPLICATION_PKCS7_SIGNATURE,
	APPLICATION_PKIX_CERT,
	APPLICATION_PKIX_CRL,
	APPLICATION_PLAIN,
	APPLICATION_POSTSCRIPT,
	APPLICATION_PRO_ENG,
	APPLICATION_RINGING_TONES,
	APPLICATION_RTF,
	APPLICATION_SDP,
	APPLICATION_SEA,
	APPLICATION_SET,
	APPLICATION_SLA,
	APPLICATION_SMIL,
	APPLICATION_SOLIDS,
	APPLICATION_SOUNDER,
	APPLICATION_STEP,
	APPLICATION_STREAMINGMEDIA,
	APPLICATION_TOOLBOOK,
	APPLICATION_VDA,
	APPLICATION_VND_FDF,
	APPLICATION_VND_HP_HPGL,
	APPLICATION_VND_HP_PCL,
	APPLICATION_VND_MS_EXCEL,
	APPLICATION_VND_MS_PKI_CERTSTORE,
	APPLICATION_VND_MS_PKI_PKO,
	APPLICATION_VND_MS_PKI_SECCAT,
	APPLICATION_VND_MS_PKI_STL,
	APPLICATION_VND_MS_POWERPOINT,
	APPLICATION_VND_MS_PROJECT,
	APPLICATION_VND_NOKIA_CONFIGURATION_MESSAGE,
	APPLICATION_VND_NOKIA_RINGING_TONE,
	APPLICATION_VND_RN_REALMEDIA,
	APPLICATION_VND_RN_REALPLAYER,
	APPLICATION_VND_WAP_WMLC,
	APPLICATION_VND_WAP_WMLSCRIPTC,
	APPLICATION_VND_XARA,
	APPLICATION_VOCALTEC_MEDIA_DESC,
	APPLICATION_VOCALTEC_MEDIA_FILE,
	APPLICATION_WORDPERFECT,
	APPLICATION_WORDPERFECT6_0,
	APPLICATION_WORDPERFECT6_1,
	APPLICATION_X_123("application/x-123"),
	APPLICATION_X_AIM("application/x-aim"),
	APPLICATION_X_AUTHORWARE_BIN("application/x-authorware-bin"),
	APPLICATION_X_AUTHORWARE_MAP("application/x-authorware-map"),
	APPLICATION_X_AUTHORWARE_SEG("application/x-authorware-seg"),
	APPLICATION_X_BCPIO("application/x-bcpio"),
	APPLICATION_X_BINARY("application/x-binary"),
	APPLICATION_X_BINHEX40("application/x-binhex40"),
	APPLICATION_X_BSH("application/x-bsh"),
	APPLICATION_X_BYTECODE_ELISP("application/x-bytecode-elisp"),
	APPLICATION_X_BZIP("application/x-bzip"),
	APPLICATION_X_BZIP2("application/x-bzip2"),
	APPLICATION_X_CDF("application/x-cdf"),
	APPLICATION_X_CHAT,
	APPLICATION_X_CMU_RASTER,
	APPLICATION_X_COCOA,
	APPLICATION_X_COMPACTPRO,
	APPLICATION_X_COMPRESS,
	APPLICATION_X_COMPRESSED,
	APPLICATION_X_CONFERENCE,
	APPLICATION_X_CPIO,
	APPLICATION_X_CPT,
	APPLICATION_X_CSH,
	APPLICATION_X_DEEPV,
	APPLICATION_X_DIRECTOR,
	APPLICATION_X_DVI,
	APPLICATION_X_ELC,
	APPLICATION_X_ENVOY,
	APPLICATION_X_ESREHBER,
	APPLICATION_X_EXCEL,
	APPLICATION_X_FRAME,
	APPLICATION_X_FREELANCE,
	APPLICATION_X_GSP,
	APPLICATION_X_GSS,
	APPLICATION_X_GTAR,
	APPLICATION_X_GZIP,
	APPLICATION_X_HDF,
	APPLICATION_X_HELPFILE,
	APPLICATION_X_HTTPD_IMAP,
	APPLICATION_X_IMA,
	APPLICATION_X_INTERNETT_SIGNUP,
	APPLICATION_X_INVENTOR,
	APPLICATION_X_IP2,
	APPLICATION_X_JAVA_CLASS,
	APPLICATION_X_JAVA_COMMERCE,
	APPLICATION_X_JAVASCRIPT,
	APPLICATION_X_KOAN,
	APPLICATION_X_KSH,
	APPLICATION_X_LATEX,
	APPLICATION_X_LHA,
	APPLICATION_X_LISP,
	APPLICATION_X_LIVESCREEN,
	APPLICATION_X_LOTUS,
	APPLICATION_X_LOTUSSCREENCAM,
	APPLICATION_X_LZH,
	APPLICATION_X_LZX,
	APPLICATION_X_MAC_BINHEX40,
	APPLICATION_X_MACBINARY,
	APPLICATION_X_MAGIC_CAP_PACKAGE_1_0,
	APPLICATION_X_MATHCAD,
	APPLICATION_X_MEME,
	APPLICATION_X_MIDI,
	APPLICATION_X_MIF,
	APPLICATION_X_MIX_TRANSFER,
	APPLICATION_X_MPLAYER2,
	APPLICATION_X_MSEXCEL,
	APPLICATION_X_MSPOWERPOINT,
	APPLICATION_X_NAVI_ANIMATION,
	APPLICATION_X_NAVIDOC,
	APPLICATION_X_NAVIMAP,
	APPLICATION_X_NAVISTYLE,
	APPLICATION_X_NETCDF,
	APPLICATION_X_NEWTON_COMPATIBLE_PKG,
	APPLICATION_X_NOKIA_9000_COMMUNICATOR_ADD_ON_SOFTWARE,
	APPLICATION_X_OMC,
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
	APPLICATION_X_POINTPLUS,
	APPLICATION_X_PORTABLE_ANYMAP,
	APPLICATION_X_PROJECT,
	APPLICATION_X_QPRO,
	APPLICATION_X_RTF,
	APPLICATION_X_SDP,
	APPLICATION_X_SEA,
	APPLICATION_X_SEELOGO,
	APPLICATION_X_SH,
	APPLICATION_X_SHAR,
	APPLICATION_X_SHOCKWAVE_FLASH,
	APPLICATION_X_SIT,
	APPLICATION_X_SPRITE,
	APPLICATION_X_STUFFIT,
	APPLICATION_X_SV4CPIO,
	APPLICATION_X_SV4CRC,
	APPLICATION_X_TAR,
	APPLICATION_X_TBOOK,
	APPLICATION_X_TCL,
	APPLICATION_X_TEX,
	APPLICATION_X_TEXINFO,
	APPLICATION_X_TROFF,
	APPLICATION_X_TROFF_MAN,
	APPLICATION_X_TROFF_ME,
	APPLICATION_X_TROFF_MS,
	APPLICATION_X_TROFF_MSVIDEO,
	APPLICATION_X_USTAR,
	APPLICATION_X_VISIO,
	APPLICATION_X_VND_AUDIOEXPLOSION_MZZ,
	APPLICATION_X_VND_LS_XPIX,
	APPLICATION_X_VRML,
	APPLICATION_X_WAIS_SOURCE,
	APPLICATION_X_WINHELP,
	APPLICATION_X_WINTALK,
	APPLICATION_X_WORLD,
	APPLICATION_X_WPWIN,
	APPLICATION_X_WRI,
	APPLICATION_X_X509_CA_CERT,
	APPLICATION_X_X509_USER_CERT,
	APPLICATION_X_ZIP_COMPRESSED,
	APPLICATION_XML,
	APPLICATION_ZIP,
	AUDIO_AIFF,
	AUDIO_BASIC,
	AUDIO_IT,
	AUDIO_MAKE,
	AUDIO_MAKE_MY_FUNK,
	AUDIO_MID,
	AUDIO_MIDI,
	AUDIO_MOD,
	AUDIO_MPEG,
	AUDIO_MPEG3,
	AUDIO_NSPAUDIO,
	AUDIO_S3M,
	AUDIO_TSP_AUDIO,
	AUDIO_TSPLAYER,
	AUDIO_VND_QCELP,
	AUDIO_VOC,
	AUDIO_VOXWARE,
	AUDIO_WAV,
	AUDIO_X_ADPCM,
	AUDIO_X_AIFF,
	AUDIO_X_AU,
	AUDIO_X_GSM,
	AUDIO_X_JAM,
	AUDIO_X_LIVEAUDIO,
	AUDIO_X_MID,
	AUDIO_X_MIDI,
	AUDIO_X_MOD,
	AUDIO_X_MPEG,
	AUDIO_X_MPEG_3,
	AUDIO_X_MPEQURL,
	AUDIO_X_NSPAUDIO,
	AUDIO_X_PN_REALAUDIO,
	AUDIO_X_PN_REALAUDIO_PLUGIN,
	AUDIO_X_PSID,
	AUDIO_X_REALAUDIO,
	AUDIO_X_TWINVQ,
	AUDIO_X_TWINVQ_PLUGIN,
	AUDIO_X_VND_AUDIOEXPLOSION_MJUICEMEDIAFILE,
	AUDIO_X_VOC,
	AUDIO_X_WAV,
	AUDIO_XM,
	IMAGE_BMP,
	IMAGE_CMU_RASTER,
	IMAGE_FIF,
	IMAGE_FLORIAN,
	IMAGE_G3FAX,
	IMAGE_GIF,
	IMAGE_IEF,
	IMAGE_JPEG,
	IMAGE_JUTVISION,
	IMAGE_NAPLPS,
	IMAGE_PICT,
	IMAGE_PJPEG,
	IMAGE_PNG,
	IMAGE_TIFF,
	IMAGE_VASA,
	IMAGE_VND_DWG,
	IMAGE_VND_FPX,
	IMAGE_VND_NET_FPX,
	IMAGE_VND_RN_REALFLASH,
	IMAGE_VND_RN_REALPIX,
	IMAGE_VND_WAP_WBMP,
	IMAGE_VND_XIFF,
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
	IMAGE_X_QUICKTIME,
	IMAGE_X_RGB,
	IMAGE_X_TIFF,
	IMAGE_X_WINDOWS_BMP,
	IMAGE_X_XBITMAP,
	IMAGE_X_XBM,
	IMAGE_X_XPIXMAP,
	IMAGE_X_XWD,
	IMAGE_X_XWINDOWDUMP,
	IMAGE_XBM,
	IMAGE_XPM,
	MESSAGE_RFC822,
	MODEL_IGES,
	MODEL_VND_DWF,
	MODEL_VRML,
	MODEL_X_POV,
	MULTIPART_X_GZIP,
	MULTIPART_X_USTAR,
	MULTIPART_X_ZIP,
	MUSIC_CRESCENDO,
	MUSIC_X_KARAOKE,
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
	TEXT_VND_ABC,
	TEXT_VND_FMI_FLEXSTOR,
	TEXT_VND_RN_REALTEXT,
	TEXT_VND_WAP_WML,
	TEXT_VND_WAP_WMLSCRIPT,
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
	VIDEO_DL,
	VIDEO_FLI,
	VIDEO_GL,
	VIDEO_MPEG,
	VIDEO_MSVIDEO,
	VIDEO_QUICKTIME,
	VIDEO_VDO,
	VIDEO_VIVO,
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