// 导出页面为PDF格式
import html2Canvas from 'html2canvas'
import JsPDF from 'jspdf'
export function getPdf(title){
  // install(Vue, options) {
    // Vue.prototype.getPdf = function(title) {
      var element = document.querySelector('#pdfDom'); // 这个dom元素是要导出pdf的div容器
      var c = document.createElement("canvas");

      var opts = {
        scale: 2, // 添加的scale 参数
        canvas: c, //自定义 canvas
        width: element.getBoundingClientRect().width * 2, //dom 原始宽度
        height: element.getBoundingClientRect().height * 2,
        useCORS: true, // 【重要】开启跨域配置
      }
      c.width = opts.width;    // 将画布宽&&高放大两倍
      c.height = opts.height;
      var context = c.getContext("2d");

      // 重要：关闭抗锯齿
      context.mozImageSmoothingEnabled = false;
      context.webkitImageSmoothingEnabled = false;
      context.msImageSmoothingEnabled = false;
      context.imageSmoothingEnabled = false;

      // 设置回原来的一倍
      context.scale(1, 1);

      setTimeout(() => {
        html2Canvas(element, opts).then(function(canvas) {
          var contentWidth = opts.width;
          var contentHeight = opts.height;

          // 一页pdf显示html页面生成的canvas高度;
          var pageHeight = contentWidth / 592.28 * 841.89;
          // 未生成pdf的html页面高度
          var leftHeight = contentHeight;
          // 页面偏移
          var position = 0;
          // a4纸的尺寸[595.28,841.89]，html页面生成的canvas在pdf中图片的宽高
          var imgWidth = 595.28;
          var imgHeight = 592.28 / contentWidth * contentHeight;

          var pageData = canvas.toDataURL('image/jpeg', 1.0);

          var pdf = new JsPDF('', 'pt', 'a4');

          // 有两个高度需要区分，一个是html页面的实际高度，和生成pdf的页面高度(841.89)
          // 当内容未超过pdf一页显示的范围，无需分页
          if (leftHeight < pageHeight) {
            pdf.addImage(pageData, 'JPEG', 0, 0, imgWidth, imgHeight);
          } else {
            while (leftHeight > 0) {
              pdf.addImage(pageData, 'JPEG', 0, position, imgWidth, imgHeight)
              leftHeight -= pageHeight;
              position -= 841.89;
              // 避免添加空白页
              if (leftHeight > 0) {
                pdf.addPage();
              }
            }
          }
          pdf.save(title + '.pdf');
        });
      });
  //   }
  // }
}