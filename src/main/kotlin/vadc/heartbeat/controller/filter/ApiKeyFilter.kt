package vadc.heartbeat.controller.filter

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ApiKeyFilter(private val apiKeyValue: String, private val authValue: String): Filter {

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        if (request is HttpServletRequest) {
            val apiKeyHeaderValue = request.getHeader(apiKey)
            val authHeaderValue = request.getHeader(authHeader)
            if (apiKeyValue != apiKeyHeaderValue && authHeaderValue != authValue) {
                (response as HttpServletResponse).status = HttpServletResponse.SC_UNAUTHORIZED
                return
            }
        }
        chain?.doFilter(request, response)
    }

    companion object {
        val apiKey = "x-api-key"
        val authHeader = "authorization"
    }
}