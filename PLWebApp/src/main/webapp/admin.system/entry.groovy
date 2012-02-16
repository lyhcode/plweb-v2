import org.plweb.webapp.helper.CommonHelper

helper = new CommonHelper(request, response)

is_admin = helper.sess('is_admin')

if (!is_admin) {
	println 'error'
	return;
}

helper.attr 'helper', helper

helper.forward 'entry.gsp'