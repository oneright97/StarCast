import * as React from 'react'
import type { SVGProps } from 'react'
const SvgLightOnIcon = (props: SVGProps<SVGSVGElement>) => (
  <svg
    xmlns='http://www.w3.org/2000/svg'
    xmlnsXlink='http://www.w3.org/1999/xlink'
    fill='none'
    viewBox='0 0 30 30'
    {...props}
  >
    <path fill='url(#light-on-icon_svg__a)' d='M0 0h30v30H0z' />
    <defs>
      <pattern
        id='light-on-icon_svg__a'
        width={1}
        height={1}
        patternContentUnits='objectBoundingBox'
      >
        <use xlinkHref='#light-on-icon_svg__b' transform='scale(.01111)' />
      </pattern>
      <image
        xlinkHref='data:image/png;base64,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'
        id='light-on-icon_svg__b'
        width={90}
        height={90}
      />
    </defs>
  </svg>
)
export default SvgLightOnIcon
