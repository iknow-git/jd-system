package com.ruoyi.framework.security.context;

import org.springframework.security.core.Authentication;

/**
 * 身份验证信息
 * 
 * @author ruoyi
 */
public class AuthenticationContextHolder
{
    private static final ThreadLocal<Authentication> contextHolder = new ThreadLocal<>();

    public static Authentication getContext()
    {
        return contextHolder.get();
    }

    public static void setContext(Authentication context)
    {
        contextHolder.set(context);
    }

    public static void clearContext()
    {
        contextHolder.remove();
    }


    
    protected final Object[][] getContents() {
        return new Object[][]{{"Rule", "& 9 < ҂ & Z < а , А< б , Б< в , В< г , Г; ґ , Ґ; ҕ , Ҕ; ѓ , Ѓ; ғ , Ғ< д , Д< ђ , Ђ< е , Е; ҽ , Ҽ; ё , Ё; ҿ , Ҿ< є , Є< ж , Ж; җ , Җ; ӂ , Ӂ< з , З; ҙ , Ҙ< ѕ , Ѕ< и , И< і , І; Ӏ < ї , Ї< й , Й< ј , Ј< к , К; ҟ , Ҟ; ӄ , Ӄ; ҝ , Ҝ; ҡ , Ҡ; ќ , Ќ; қ , Қ< л , Л< љ , Љ< м , М< н , Н; ѣ ; ң , Ң; ҥ , Ҥ; һ , Һ; ӈ , Ӈ< њ , Њ< о , О; ҩ , Ҩ< п , П; ҧ , Ҧ< р , Р< с , С; ҫ , Ҫ< т , Т; ҭ , Ҭ< ћ , Ћ< у , У; ү , Ү< ў , Ў< ұ , Ұ< ф , Ф< х , Х; ҳ , Ҳ< ц , Ц; ҵ , Ҵ< ч , Ч; ҷ ; Ҷ; ҹ , Ҹ; ӌ , Ӌ< џ , Џ< ш , Ш< щ , Щ< ъ , Ъ< ы , Ы< ь , Ь< э , Э< ю , Ю< я , Я< ѡ , Ѡ< Ѣ < ѥ , Ѥ< ѧ , Ѧ< ѩ , Ѩ< ѫ , Ѫ< ѭ , Ѭ< ѯ , Ѯ< ѱ , Ѱ< ѳ , Ѳ< ѵ , Ѵ; ѷ , Ѷ< ѹ , Ѹ< ѻ , Ѻ< ѽ , Ѽ< ѿ , Ѿ< ҁ , Ҁ"}};
    }
}
