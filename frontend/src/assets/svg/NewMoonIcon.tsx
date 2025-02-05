import type { SVGProps } from 'react'
const SvgNewMoonIcon = (props: SVGProps<SVGSVGElement>) => (
  <svg
    xmlns='http://www.w3.org/2000/svg'
    xmlnsXlink='http://www.w3.org/1999/xlink'
    fill='none'
    viewBox='0 0 30 30'
    {...props}
  >
    <path fill='url(#new-moon-icon_svg__a)' d='M0 0h30v30H0z' />
    <defs>
      <pattern
        id='new-moon-icon_svg__a'
        width={1}
        height={1}
        patternContentUnits='objectBoundingBox'
      >
        <use xlinkHref='#new-moon-icon_svg__b' transform='scale(.01)' />
      </pattern>
      <image
        xlinkHref='data:image/png;base64,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'
        id='new-moon-icon_svg__b'
        width={100}
        height={100}
      />
    </defs>
  </svg>
)
export default SvgNewMoonIcon
