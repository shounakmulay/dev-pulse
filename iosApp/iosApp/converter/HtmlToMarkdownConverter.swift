//
// Created by Shounak Mulay on 31/07/26.
//

import Foundation
import ComposeApp
import HtmlToMarkdown

class HtmlToMarkdownConverter: HtmlToMarkdownConverterProtocol {
    func convert(html: String) -> String? {
         do {
            let result = try HtmlToMarkdown.convert(html: html, options: nil)
            let markdown = result.content()?.toString()
            return markdown
        } catch {
            return nil
        }
    }
}
