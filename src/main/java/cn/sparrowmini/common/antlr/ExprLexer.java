// Generated from Expr.g4 by ANTLR 4.13.2
package cn.sparrowmini.common.antlr;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class ExprLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, AND=11, OR=12, NOT=13, IS=14, NULL=15, IN=16, ID=17, STRING=18, 
		NUMBER=19, WS=20;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
			"T__9", "AND", "OR", "NOT", "IS", "NULL", "IN", "ID", "STRING", "NUMBER", 
			"WS"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'('", "')'", "'='", "'!='", "'<'", "'<='", "'>'", "'>='", "'like'", 
			"','", "'and'", "'or'", "'not'", "'is'", "'null'", "'in'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, "AND", 
			"OR", "NOT", "IS", "NULL", "IN", "ID", "STRING", "NUMBER", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public ExprLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Expr.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\u0004\u0000\u0014\u007f\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002"+
		"\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002"+
		"\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002"+
		"\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002"+
		"\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e"+
		"\u0002\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011"+
		"\u0002\u0012\u0007\u0012\u0002\u0013\u0007\u0013\u0001\u0000\u0001\u0000"+
		"\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001"+
		"\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001"+
		"\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001\f"+
		"\u0001\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010"+
		"\u0005\u0010\\\b\u0010\n\u0010\f\u0010_\t\u0010\u0001\u0011\u0001\u0011"+
		"\u0001\u0011\u0001\u0011\u0005\u0011e\b\u0011\n\u0011\f\u0011h\t\u0011"+
		"\u0001\u0011\u0001\u0011\u0001\u0012\u0004\u0012m\b\u0012\u000b\u0012"+
		"\f\u0012n\u0001\u0012\u0001\u0012\u0004\u0012s\b\u0012\u000b\u0012\f\u0012"+
		"t\u0003\u0012w\b\u0012\u0001\u0013\u0004\u0013z\b\u0013\u000b\u0013\f"+
		"\u0013{\u0001\u0013\u0001\u0013\u0000\u0000\u0014\u0001\u0001\u0003\u0002"+
		"\u0005\u0003\u0007\u0004\t\u0005\u000b\u0006\r\u0007\u000f\b\u0011\t\u0013"+
		"\n\u0015\u000b\u0017\f\u0019\r\u001b\u000e\u001d\u000f\u001f\u0010!\u0011"+
		"#\u0012%\u0013\'\u0014\u0001\u0000\u0005\u0003\u0000AZ__az\u0004\u0000"+
		"09AZ__az\u0002\u0000\'\'\\\\\u0001\u000009\u0003\u0000\t\n\r\r  \u0085"+
		"\u0000\u0001\u0001\u0000\u0000\u0000\u0000\u0003\u0001\u0000\u0000\u0000"+
		"\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000"+
		"\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000"+
		"\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011"+
		"\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015"+
		"\u0001\u0000\u0000\u0000\u0000\u0017\u0001\u0000\u0000\u0000\u0000\u0019"+
		"\u0001\u0000\u0000\u0000\u0000\u001b\u0001\u0000\u0000\u0000\u0000\u001d"+
		"\u0001\u0000\u0000\u0000\u0000\u001f\u0001\u0000\u0000\u0000\u0000!\u0001"+
		"\u0000\u0000\u0000\u0000#\u0001\u0000\u0000\u0000\u0000%\u0001\u0000\u0000"+
		"\u0000\u0000\'\u0001\u0000\u0000\u0000\u0001)\u0001\u0000\u0000\u0000"+
		"\u0003+\u0001\u0000\u0000\u0000\u0005-\u0001\u0000\u0000\u0000\u0007/"+
		"\u0001\u0000\u0000\u0000\t2\u0001\u0000\u0000\u0000\u000b4\u0001\u0000"+
		"\u0000\u0000\r7\u0001\u0000\u0000\u0000\u000f9\u0001\u0000\u0000\u0000"+
		"\u0011<\u0001\u0000\u0000\u0000\u0013A\u0001\u0000\u0000\u0000\u0015C"+
		"\u0001\u0000\u0000\u0000\u0017G\u0001\u0000\u0000\u0000\u0019J\u0001\u0000"+
		"\u0000\u0000\u001bN\u0001\u0000\u0000\u0000\u001dQ\u0001\u0000\u0000\u0000"+
		"\u001fV\u0001\u0000\u0000\u0000!Y\u0001\u0000\u0000\u0000#`\u0001\u0000"+
		"\u0000\u0000%l\u0001\u0000\u0000\u0000\'y\u0001\u0000\u0000\u0000)*\u0005"+
		"(\u0000\u0000*\u0002\u0001\u0000\u0000\u0000+,\u0005)\u0000\u0000,\u0004"+
		"\u0001\u0000\u0000\u0000-.\u0005=\u0000\u0000.\u0006\u0001\u0000\u0000"+
		"\u0000/0\u0005!\u0000\u000001\u0005=\u0000\u00001\b\u0001\u0000\u0000"+
		"\u000023\u0005<\u0000\u00003\n\u0001\u0000\u0000\u000045\u0005<\u0000"+
		"\u000056\u0005=\u0000\u00006\f\u0001\u0000\u0000\u000078\u0005>\u0000"+
		"\u00008\u000e\u0001\u0000\u0000\u00009:\u0005>\u0000\u0000:;\u0005=\u0000"+
		"\u0000;\u0010\u0001\u0000\u0000\u0000<=\u0005l\u0000\u0000=>\u0005i\u0000"+
		"\u0000>?\u0005k\u0000\u0000?@\u0005e\u0000\u0000@\u0012\u0001\u0000\u0000"+
		"\u0000AB\u0005,\u0000\u0000B\u0014\u0001\u0000\u0000\u0000CD\u0005a\u0000"+
		"\u0000DE\u0005n\u0000\u0000EF\u0005d\u0000\u0000F\u0016\u0001\u0000\u0000"+
		"\u0000GH\u0005o\u0000\u0000HI\u0005r\u0000\u0000I\u0018\u0001\u0000\u0000"+
		"\u0000JK\u0005n\u0000\u0000KL\u0005o\u0000\u0000LM\u0005t\u0000\u0000"+
		"M\u001a\u0001\u0000\u0000\u0000NO\u0005i\u0000\u0000OP\u0005s\u0000\u0000"+
		"P\u001c\u0001\u0000\u0000\u0000QR\u0005n\u0000\u0000RS\u0005u\u0000\u0000"+
		"ST\u0005l\u0000\u0000TU\u0005l\u0000\u0000U\u001e\u0001\u0000\u0000\u0000"+
		"VW\u0005i\u0000\u0000WX\u0005n\u0000\u0000X \u0001\u0000\u0000\u0000Y"+
		"]\u0007\u0000\u0000\u0000Z\\\u0007\u0001\u0000\u0000[Z\u0001\u0000\u0000"+
		"\u0000\\_\u0001\u0000\u0000\u0000][\u0001\u0000\u0000\u0000]^\u0001\u0000"+
		"\u0000\u0000^\"\u0001\u0000\u0000\u0000_]\u0001\u0000\u0000\u0000`f\u0005"+
		"\'\u0000\u0000ae\b\u0002\u0000\u0000bc\u0005\\\u0000\u0000ce\t\u0000\u0000"+
		"\u0000da\u0001\u0000\u0000\u0000db\u0001\u0000\u0000\u0000eh\u0001\u0000"+
		"\u0000\u0000fd\u0001\u0000\u0000\u0000fg\u0001\u0000\u0000\u0000gi\u0001"+
		"\u0000\u0000\u0000hf\u0001\u0000\u0000\u0000ij\u0005\'\u0000\u0000j$\u0001"+
		"\u0000\u0000\u0000km\u0007\u0003\u0000\u0000lk\u0001\u0000\u0000\u0000"+
		"mn\u0001\u0000\u0000\u0000nl\u0001\u0000\u0000\u0000no\u0001\u0000\u0000"+
		"\u0000ov\u0001\u0000\u0000\u0000pr\u0005.\u0000\u0000qs\u0007\u0003\u0000"+
		"\u0000rq\u0001\u0000\u0000\u0000st\u0001\u0000\u0000\u0000tr\u0001\u0000"+
		"\u0000\u0000tu\u0001\u0000\u0000\u0000uw\u0001\u0000\u0000\u0000vp\u0001"+
		"\u0000\u0000\u0000vw\u0001\u0000\u0000\u0000w&\u0001\u0000\u0000\u0000"+
		"xz\u0007\u0004\u0000\u0000yx\u0001\u0000\u0000\u0000z{\u0001\u0000\u0000"+
		"\u0000{y\u0001\u0000\u0000\u0000{|\u0001\u0000\u0000\u0000|}\u0001\u0000"+
		"\u0000\u0000}~\u0006\u0013\u0000\u0000~(\u0001\u0000\u0000\u0000\b\u0000"+
		"]dfntv{\u0001\u0006\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}