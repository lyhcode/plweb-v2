$(document).ready(function() {
	var __html_css_overflow = '';
	$(document).bind('cbox_open', function () {
		__html_css_overflow = $('html').css('overflow');
		$('html').css({overflow: 'hidden'});
	}).bind('cbox_closed', function () {
		$('html').css({overflow: __html_css_overflow});
	});

	// Use colorbox iframe for all embedded links.
	$('a.embedded-link').colorbox({iframe: true, width: '95%', height: '95%'});
	$('a.embedded-slide').colorbox({slideshow: false});

	var login_init = function() {
		$('input.input-text').tipsy({trigger: 'focus', gravity: 'w'});
		
		var check_by_ajax = function(e) {
		    $('input[name=email]').removeClass('correct');
		    $('input[name=email]').removeClass('error');
		    
			$.ajax({
				type: 'post',
				dataType: 'json',
				url : $('input[name="login_ajax_url"]').val(),
				data: {
					email: $('input[name=email]').val(),
					password: $('input[name=password]').val()
				},
				error : function(xhr) {
				},
				success : function(o) {
				    if ($('input[name=email]').val() != '') {
                        if (!o.email_ok) {
                            $('input[name=email]').addClass('error');
                        }
                        else {
                            $('input[name=email]').addClass('correct');
                        }
                    }
				}
			});
		};
		
		$('input[name=email], input[name=password]').change(check_by_ajax);
		
		if ($('input[name=email]').val() == '') {
			$('input[name=email]').focus();
		}
		else {
			$('input[name=password]').focus();
		}
		
		if ($('input[name=email]').val() != '' || $('input[name=password]').val() != '') {
		    check_by_ajax();
		}
	};
	
	var signup_init = function() {
		$('input.input-text').tipsy({trigger: 'focus', gravity: 'w'});
		
		$('input[name=roletype]').click(function() {
		        var v = $(this).val();
		        $('.roletype-desc').hide();
		        $('#roletype-desc-'+v).show();
		});
		
		var v = $('input[name=roletype]:checked').val();
		$('.roletype-desc').hide();
        $('#roletype-desc-'+v).show();
	};
	
	var account_init = function() {
	    $('input.input-text').tipsy({trigger: 'focus', gravity: 'w'});

	    	$('.pre-encode').each(function() {
            var t = $(this).text();
            $(this).text(t.replace(' * ', '@'));
        });
	};
	
    var password_init = function() {
	    $('input.input-text').tipsy({trigger: 'focus', gravity: 'w'});
	};
	
	var m = $('input[name=module]').val();
	if (m) {
		eval('if ('+m+'_init) '+m+'_init();');
	}	
});
